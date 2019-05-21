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
package com.ly.train.flower.db.mysql.codec.util;

import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.Result;
import com.ly.train.flower.db.api.ResultHandler;
import com.ly.train.flower.db.api.support.OneArgFunction;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.MySqlPreparedStatement;
import com.ly.train.flower.db.mysql.codec.MysqlResult;
import com.ly.train.flower.db.mysql.codec.decoder.AcceptNextResponseDecoder;
import com.ly.train.flower.db.mysql.codec.decoder.OKResponseDecoder;
import com.ly.train.flower.db.mysql.codec.decoder.ExpectPreparQueryDecoder;
import com.ly.train.flower.db.mysql.codec.decoder.ExpectQueryResultDecoder;
import com.ly.train.flower.db.mysql.codec.decoder.ExpectStatementResultDecoder;
import com.ly.train.flower.db.mysql.codec.decoder.ExpectUpdateResultDecoder;
import com.ly.train.flower.db.mysql.codec.decoder.RowDecoder;
import com.ly.train.flower.db.mysql.codec.model.MySqlRequest;
import com.ly.train.flower.db.mysql.codec.packets.Command;
import com.ly.train.flower.db.mysql.codec.packets.request.ClosePreparedStatementRequest;
import com.ly.train.flower.db.mysql.codec.packets.request.CommandRequest;
import com.ly.train.flower.db.mysql.codec.packets.request.PreparedStatementRequest;
import com.ly.train.flower.db.mysql.codec.packets.request.StringCommandRequest;
import com.ly.train.flower.db.mysql.codec.packets.response.StatementPreparedEOFResponse;


public final class MySqlRequestUtil {
    private static final OneArgFunction<MysqlResult, Void> TO_VOID = arg -> null;

    public static MySqlRequest<?> createCloseRequest(
            MySqlConnection connection,
            DbCallback<Void> callback,
            StackTraceElement[] entry) {
        return new MySqlRequest<>(
                "Close",
                new OKResponseDecoder<>(connection, callback, entry),
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
