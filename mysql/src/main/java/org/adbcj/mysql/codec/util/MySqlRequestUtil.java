package org.adbcj.mysql.codec.util;

import org.adbcj.DbCallback;
import org.adbcj.Result;
import org.adbcj.ResultHandler;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.MySqlPreparedStatement;
import org.adbcj.mysql.codec.MysqlResult;
import org.adbcj.mysql.codec.decoder.AcceptNextResponseDecoder;
import org.adbcj.mysql.codec.decoder.ExpectOKDecoder;
import org.adbcj.mysql.codec.decoder.ExpectPreparQueryDecoder;
import org.adbcj.mysql.codec.decoder.ExpectQueryResultDecoder;
import org.adbcj.mysql.codec.decoder.ExpectStatementResultDecoder;
import org.adbcj.mysql.codec.decoder.ExpectUpdateResultDecoder;
import org.adbcj.mysql.codec.decoder.RowDecoder;
import org.adbcj.mysql.codec.model.MySqlRequest;
import org.adbcj.mysql.codec.packets.Command;
import org.adbcj.mysql.codec.packets.request.ClosePreparedStatementRequest;
import org.adbcj.mysql.codec.packets.request.CommandRequest;
import org.adbcj.mysql.codec.packets.request.PreparedStatementRequest;
import org.adbcj.mysql.codec.packets.request.StringCommandRequest;
import org.adbcj.mysql.codec.packets.response.StatementPreparedEOFResponse;
import org.adbcj.support.OneArgFunction;


public final class MySqlRequestUtil {
    private static final OneArgFunction<MysqlResult, Void> TO_VOID = arg -> null;

    public static MySqlRequest<?> createCloseRequest(
            MySqlConnection connection,
            DbCallback<Void> callback,
            StackTraceElement[] entry) {
        return new MySqlRequest<>(
                "Close",
                new ExpectOKDecoder<>(connection, callback, entry),
                new CommandRequest(Command.QUIT),
                callback);
    }

    public static <T> MySqlRequest<T> executeQuery(
            MySqlConnection connection,
            String query,
            ResultHandler<T> eventHandler,
            T accumulator,
            DbCallback<T> callback,
            StackTraceElement[] entry) {
        return new MySqlRequest<>(
                "Query: " + query,
                new ExpectQueryResultDecoder<T>(
                        connection,
                        RowDecoder.RowDecodingType.STRING_BASED,
                        eventHandler,
                        accumulator,
                        callback,
                        entry),
                new StringCommandRequest(Command.QUERY, query),
                callback);
    }

    public static <T> MySqlRequest<T> executePreparedQuery(
            MySqlConnection connection,
            StatementPreparedEOFResponse stmp,
            Object[] data,
            ResultHandler<T> eventHandler,
            T accumulator,
            DbCallback<T> callback,
            StackTraceElement[] entry) {
        return new MySqlRequest<>(
                "Execute-Statement",
                new ExpectStatementResultDecoder<>(
                        connection,
                        RowDecoder.RowDecodingType.BINARY,
                        eventHandler,
                        accumulator,
                        callback,
                        entry),
                new PreparedStatementRequest(stmp.getHandlerId(), stmp.getParametersTypes(), data),
                callback);
    }

    public static MySqlRequest<?> executeUpdate(
            MySqlConnection connection,
            String sql,
            DbCallback<Result> callback,
            StackTraceElement[] entry) {
        return new MySqlRequest<>(
                "Update: " + sql,
                new ExpectUpdateResultDecoder<>(connection, callback, entry),
                new StringCommandRequest(Command.QUERY, sql),
                callback);
    }

    public static MySqlRequest<?> prepareQuery(
            MySqlConnection connection,
            String sql,
            DbCallback<MySqlPreparedStatement> callback,
            StackTraceElement[] entry) {

        return new MySqlRequest<>("Prepare-Query: " + sql,
                new ExpectPreparQueryDecoder(connection, callback, entry),
                new StringCommandRequest(Command.STATEMENT_PREPARE, sql),
                callback);
    }

    public static MySqlRequest<?> closeStatemeent(
            MySqlConnection connection,
            StatementPreparedEOFResponse statementInfo,
            DbCallback<Void> callback) {
        return new MySqlRequest<>(
                "Close-Statement: ",
                new AcceptNextResponseDecoder(connection),
                new ClosePreparedStatementRequest(statementInfo.getHandlerId()), callback);
    }

    public static MySqlRequest<?> beginTransaction(MySqlConnection connection,
                                                DbCallback<Void> callback,
                                                StackTraceElement[] entry) {

        return new MySqlRequest<>(
                "Begin-Transaction: ",
                new ExpectUpdateResultDecoder<>(connection, callback, entry, TO_VOID),
                new StringCommandRequest(Command.QUERY, "begin"),
                callback);
    }

    public static MySqlRequest<?> commitTransaction(MySqlConnection connection,
                                                 DbCallback<Void> callback,
                                                 StackTraceElement[] entry) {
        return new MySqlRequest<>(
                "Commit-Transaction: ",
                new ExpectUpdateResultDecoder<>(connection, callback, entry, TO_VOID),
                new StringCommandRequest(Command.QUERY, "commit"),
                callback);
    }

    public static MySqlRequest<?> rollbackTransaction(MySqlConnection connection,
                                                   DbCallback<Void> callback,
                                                   StackTraceElement[] entry) {
        return new MySqlRequest<>(
                "Rollback-Transaction: ",
                new ExpectUpdateResultDecoder<>(connection, callback, entry, TO_VOID),
                new StringCommandRequest(Command.QUERY, "rollback"),
                callback);
    }
}
