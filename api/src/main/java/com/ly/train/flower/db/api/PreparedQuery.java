package com.ly.train.flower.db.api;

import java.util.concurrent.CompletableFuture;
import com.ly.train.flower.db.api.support.DbCompletableFuture;
import com.ly.train.flower.db.api.support.DefaultResultEventsHandler;
import com.ly.train.flower.db.api.support.DefaultResultSet;

public interface PreparedQuery extends PreparedStatement {

  default CompletableFuture<ResultSet> execute(Object... params) {
    DefaultResultEventsHandler handler = new DefaultResultEventsHandler();
    DefaultResultSet acc = new DefaultResultSet();
    DbCompletableFuture<DefaultResultSet> future = new DbCompletableFuture<>();
    executeWithCallback(handler, acc, future, params);
    return (CompletableFuture) future;
  }

  default <T> CompletableFuture<T> executeWithCallback(ResultHandler<T> handler, T accumulator, Object... params) {
    DbCompletableFuture<T> future = new DbCompletableFuture<>();
    executeWithCallback(handler, accumulator, future, params);
    return future;
  }

  <T> void executeWithCallback(ResultHandler<T> handler, T accumulator, DbCallback<T> callback, Object... params);
}
