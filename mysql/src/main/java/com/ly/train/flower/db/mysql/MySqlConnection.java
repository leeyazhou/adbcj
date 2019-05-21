/**
 * Copyright © 2019 yazhou.li (lee_yazhou@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.db.mysql;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.CloseMode;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbConnectionClosedException;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.PreparedQuery;
import com.ly.train.flower.db.api.PreparedUpdate;
import com.ly.train.flower.db.api.Result;
import com.ly.train.flower.db.api.ResultHandler;
import com.ly.train.flower.db.api.StandardProperties;
import com.ly.train.flower.db.api.support.CloseOnce;
import com.ly.train.flower.db.api.support.LoginCredentials;
import com.ly.train.flower.db.api.support.stacktracing.StackTracingOptions;
import com.ly.train.flower.db.mysql.codec.model.ClientCapability;
import com.ly.train.flower.db.mysql.codec.model.ClientCapabilityExtend;
import com.ly.train.flower.db.mysql.codec.model.MySqlRequest;
import com.ly.train.flower.db.mysql.codec.util.MySqlRequestUtil;
import io.netty.channel.Channel;

public class MySqlConnection implements Connection {

  private static final Logger logger = LoggerFactory.getLogger(MySqlConnection.class);

  private final LoginCredentials login;
  private final int maxQueueSize;
  private final MysqlConnectionManager connectionManager;
  private final Channel channel;

  protected final int id;
  final StackTracingOptions strackTraces;

  private final ArrayDeque<MySqlRequest<?>> requestQueue;

  private final Object lock = new Object();
  private final CloseOnce closer = new CloseOnce();
  private volatile boolean isInTransaction = false;

  public MySqlConnection(LoginCredentials login, int maxQueueSize, MysqlConnectionManager connectionManager,
      Channel channel, StackTracingOptions strackTraces) {
    this.login = login;
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


  @Override
  public void beginTransaction(DbCallback<Void> callback) {
    if (isInTransaction()) {
      throw new DbException("This connection is already in a transaction");
    }
    checkClosed();
    StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
    synchronized (lock) {
      forceQueRequest(MySqlRequestUtil.beginTransaction(this, callback, entry));
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
      if (failIfQueueFull(MySqlRequestUtil.commitTransaction(this, callback, entry))) {
        isInTransaction = false;
      }
    }
  }


  @Override
  public void rollback(DbCallback<Void> callback) {
    if (!isInTransaction()) {
      throw new DbException("No transaction has been started to rollback");
    }
    checkClosed();
    StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
    doRollback(entry, callback);
  }

  private void doRollback(StackTraceElement[] entry, DbCallback<Void> callback) {
    synchronized (lock) {
      if (failIfQueueFull(MySqlRequestUtil.rollbackTransaction(this, callback, entry))) {
        isInTransaction = false;
      }
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
    failIfQueueFull(MySqlRequestUtil.executeQuery(this, sql, eventHandler, accumulator, callback, entry));
  }


  @Override
  public void executeUpdate(String sql, DbCallback<Result> callback) {
    checkClosed();
    StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
    failIfQueueFull(MySqlRequestUtil.executeUpdate(this, sql, callback, entry));

  }


  @Override
  public void prepareQuery(String sql, DbCallback<PreparedQuery> callback) {
    checkClosed();
    StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
    failIfQueueFull(MySqlRequestUtil.prepareQuery(this, sql, (DbCallback) callback, entry));
  }

  @Override
  public void prepareUpdate(String sql, DbCallback<PreparedUpdate> callback) {
    checkClosed();
    StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
    failIfQueueFull(MySqlRequestUtil.prepareQuery(this, sql, (DbCallback) callback, entry));
  }

  @Override
  public void close(CloseMode closeMode, DbCallback<Void> callback) throws DbException {
    StackTraceElement[] entry = strackTraces.captureStacktraceAtEntryPoint();
    synchronized (lock) {
      closer.requestClose(callback, () -> {
        // Close the connection forcibly when IO error such as 'Too many connections'
        // even if using connection-pool.
        // @since 2017-09-02 little-pan
        if (connectionManager.getConnectionPool() == null || CloseMode.CLOSE_FORCIBLY == closeMode) {
          doActualClose(closeMode, entry);
        } else {
          doRollback(entry, (result, failure) -> {
            if (failure == null) {
              if (this.connectionManager.isClosed()) {
                doActualClose(closeMode, entry);
              } else {
                channel.pipeline().remove(MysqlConnectionManager.DECODER);
                connectionManager.getConnectionPool().release(login, channel);
                callback.onComplete(result, null);
              }
            } else {
              callback.onComplete(null,
                  new DbException("Failed to rollback transaction and return connection to pool", failure));
            }
          });
        }
      });
    }
  }

  private void doActualClose(CloseMode closeMode, StackTraceElement[] entry) {
    if (closeMode == CloseMode.CLOSE_FORCIBLY) {
      // Close the connection forcibly
      forceCloseOnPendingRequests();
      realClose(entry);
      return;
    }
    if (closeMode == CloseMode.CANCEL_PENDING_OPERATIONS) {
      forceCloseOnPendingRequests();
    }
    final MySqlRequest<?> closeRequest =
        MySqlRequestUtil.createCloseRequest(this, (res, error) -> tryCompleteClose(error), entry);
    forceQueRequest(closeRequest);
  }

  private void realClose(final StackTraceElement[] entry) {
    final Channel ch = channel;
    ch.close().addListener((f) -> {
      logger.debug("Real close channel#{}", ch.id());
      final DbException failure;
      if (f.cause() == null) {
        failure = null;
      } else {
        failure = DbException.wrap(f.cause(), entry);
      }
      tryCompleteClose(failure);
    });
  }

  public void tryCompleteClose(DbException error) {
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

  private static final Set<ClientCapability> clientCapabilities =
      EnumSet.of(ClientCapability.LONG_PASSWORD, ClientCapability.FOUND_ROWS, ClientCapability.LONG_COLUMN_FLAG,
          ClientCapability.CONNECT_WITH_DB, ClientCapability.LOCAL_FILES, ClientCapability.PROTOCOL_4_1,
          ClientCapability.TRANSACTIONS, ClientCapability.SECURE_CONNECTION);

  public static Set<ClientCapability> getClientCapabilities() {
    return clientCapabilities;
  }

  private static final Set<ClientCapabilityExtend> clientCapabilityExtends =
      EnumSet.of(ClientCapabilityExtend.MULTI_RESULTS);

  public static Set<ClientCapabilityExtend> getExtendedClientCapabilities() {
    return clientCapabilityExtends;
  }

  boolean failIfQueueFull(MySqlRequest<?> request) {
    synchronized (lock) {
      int requestsPending = requestQueue.size();
      if (requestsPending > maxQueueSize) {
        DbException ex = new DbException("To many pending requests. The current maximum is " + maxQueueSize
            + ". Ensure that your not overloading the database with requests. " + "Also check the "
            + StandardProperties.MAX_QUEUE_LENGTH + " property");
        request.getCallback().onComplete(null, ex);
        return false;
      }
      forceQueRequest(request);
      return true;
    }
  }

  MySqlRequest<?> forceQueRequest(MySqlRequest<?> request) {
    synchronized (lock) {
      this.requestQueue.add(request);
      this.channel.writeAndFlush(request.getRequest());
      return request;
    }
  }

  public MySqlRequest<?> dequeRequest() {
    synchronized (lock) {
      final MySqlRequest<?> request = requestQueue.poll();
      if (logger.isDebugEnabled()) {
        logger.debug("Dequeued request: {}", request);
      }
      return request;
    }
  }


  private void forceCloseOnPendingRequests() {
    DbConnectionClosedException closed = new DbConnectionClosedException("Connection is closed");
    for (MySqlRequest<?> request : requestQueue) {
      request.getCallback().onComplete(null, closed);

    }
  }
}
