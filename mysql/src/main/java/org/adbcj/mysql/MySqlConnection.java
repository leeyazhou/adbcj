package org.adbcj.mysql;

import org.adbcj.*;
import org.adbcj.mysql.codec.ClientCapabilities;
import org.adbcj.mysql.codec.ExtendedClientCapabilities;
import org.adbcj.mysql.codec.MySqlRequest;
import org.adbcj.mysql.codec.MySqlRequests;
import io.netty.channel.Channel;
import org.adbcj.support.CloseOnce;
import org.adbcj.support.stacktracing.StackTracingOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MySqlConnection implements Connection {

    private static final Logger logger = LoggerFactory.getLogger(MySqlConnection.class);

    private final int maxQueueSize;
    private final MysqlConnectionManager connectionManager;
    private final Channel channel;

    protected final int id;
    final StackTracingOptions strackTraces;

    private final ArrayDeque<MySqlRequest> requestQueue;

    private final Object lock = new Object();
    private final CloseOnce closer = new CloseOnce();
    private volatile boolean isInTransaction = false;

    public MySqlConnection(
            int maxQueueSize,
            MysqlConnectionManager connectionManager,
            Channel channel,
            StackTracingOptions strackTraces) {
        this.maxQueueSize = maxQueueSize;
        this.connectionManager = connectionManager;
        this.channel = channel;
        this.id = connectionManager.nextId();
        this.strackTraces = strackTraces;

        synchronized (lock) {
            requestQueue = new ArrayDeque<>(maxQueueSize + 1);
        }
    }


    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public synchronized CompletableFuture<Void> close() throws DbException {
        return close(CloseMode.CLOSE_GRACEFULLY);
    }

    @Override
    public void beginTransaction(DbCallback<Void> callback) {
        if (isInTransaction()) {
            throw new DbException("This connection is already in a transaction");
        }
        checkClosed();
        StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
        synchronized (lock) {
            forceQueRequest(MySqlRequests.beginTransaction(this, callback, entry));
            isInTransaction = true;
        }
    }

    @Override
    public void commit(DbCallback<Void> callback) {
        if (!isInTransaction()) {
            throw new DbException("No transaction has been started to commit");
        }
        checkClosed();
        StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
        synchronized (lock) {
            queRequest(MySqlRequests.commitTransaction(this, callback, entry));
            isInTransaction = false;
        }
    }


    @Override
    public void rollback(DbCallback<Void> callback) {
        if (!isInTransaction()) {
            throw new DbException("No transaction has been started to rollback");
        }
        checkClosed();
        StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
        synchronized (lock) {
            queRequest(MySqlRequests.rollbackTransaction(this, callback, entry));
            isInTransaction = false;
        }
    }

    @Override
    public boolean isInTransaction() {
        return isInTransaction;
    }


    @Override
    public <T> void executeQuery(String sql, ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback) {
        checkClosed();
        StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
        queRequest(MySqlRequests.executeQuery(
                this,
                sql,
                eventHandler,
                accumulator,
                callback,
                entry));
    }


    @Override
    public void executeUpdate(String sql, DbCallback<Result> callback) {
        checkClosed();
        StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
        queRequest(
                MySqlRequests.executeUpdate(this,
                        sql,
                        callback,
                        entry
                ));

    }


    @Override
    public void prepareQuery(String sql, DbCallback<PreparedQuery> callback) {
        checkClosed();
        StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
        queRequest(MySqlRequests.prepareQuery(this, sql, (DbCallback) callback, entry));
    }

    @Override
    public void prepareUpdate(String sql, DbCallback<PreparedUpdate> callback) {
        checkClosed();
        StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
        queRequest(MySqlRequests.prepareQuery(this, sql, (DbCallback) callback, entry));
    }

    @Override
    public void close(CloseMode closeMode, DbCallback<Void> callback) throws DbException {
        synchronized (lock) {
            closer.requestClose(callback, () -> {
                if (closeMode == CloseMode.CANCEL_PENDING_OPERATIONS) {
                    forceCloseOnPendingRequests();
                }
                StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
                if (closeMode == CloseMode.CANCEL_PENDING_OPERATIONS) {
                    forceCloseOnPendingRequests();
                }
                final MySqlRequest closeRequest = MySqlRequests.createCloseRequest(
                        this,
                        (res, error) -> tryCompleteClose(error),
                        entry);
                forceQueRequest(closeRequest);
            });
        }
    }

    void tryCompleteClose(DbException error) {
        synchronized (lock) {
            connectionManager.closedConnect(this);
            closer.didClose(error);
        }
    }


    @Override
    public boolean isClosed() throws DbException {
        return closer.isClose();
    }


    void checkClosed() {
        if (isClosed()) {
            throw new DbConnectionClosedException("This connection is closed");
        }
    }

    private static final Set<ClientCapabilities> CLIENT_CAPABILITIES = EnumSet.of(
            ClientCapabilities.LONG_PASSWORD,
            ClientCapabilities.FOUND_ROWS,
            ClientCapabilities.LONG_COLUMN_FLAG,
            ClientCapabilities.CONNECT_WITH_DB,
            ClientCapabilities.LOCAL_FILES,
            ClientCapabilities.PROTOCOL_4_1,
            ClientCapabilities.TRANSACTIONS,
            ClientCapabilities.SECURE_AUTHENTICATION);

    public Set<ClientCapabilities> getClientCapabilities() {
        return CLIENT_CAPABILITIES;
    }

    private static final Set<ExtendedClientCapabilities> EXTENDED_CLIENT_CAPABILITIES = EnumSet.of(
            ExtendedClientCapabilities.MULTI_RESULTS
    );

    public Set<ExtendedClientCapabilities> getExtendedClientCapabilities() {
        return EXTENDED_CLIENT_CAPABILITIES;
    }

    MySqlRequest queRequest(MySqlRequest request) {
        synchronized (lock) {

            int requestsPending = requestQueue.size();
            if (requestsPending > maxQueueSize) {
                throw new DbException("To many pending requests. The current maximum is " + maxQueueSize + "." +
                        "Ensure that your not overloading the database with requests. " +
                        "Also check the " + StandardProperties.MAX_QUEUE_LENGTH + " property");
            }
            return forceQueRequest(request);
        }
    }

    MySqlRequest forceQueRequest(MySqlRequest request) {
        synchronized (lock) {
            requestQueue.add(request);
            channel.writeAndFlush(request.request);
            return request;
        }
    }

    public MySqlRequest dequeRequest() {
        synchronized (lock) {
            final MySqlRequest request = requestQueue.poll();
            if (logger.isDebugEnabled()) {
                logger.debug("Dequeued request: {}", request);
            }
            return request;
        }
    }


    private void forceCloseOnPendingRequests() {
        DbConnectionClosedException closed = new DbConnectionClosedException("Connection is closed");
        for (MySqlRequest request : requestQueue) {
            request.callback.onComplete(null, closed);

        }
    }
}