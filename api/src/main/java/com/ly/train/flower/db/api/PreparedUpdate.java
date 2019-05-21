package com.ly.train.flower.db.api;

import java.util.concurrent.CompletableFuture;
import com.ly.train.flower.db.api.support.DbCompletableFuture;

public interface PreparedUpdate extends PreparedStatement {

  default CompletableFuture<Result> execute(Object... params) {
    DbCompletableFuture<Result> future = new DbCompletableFuture<>();
    execute(future, params);
    return future;
  }

  void execute(DbCallback<Result> callback, Object... params);
}
