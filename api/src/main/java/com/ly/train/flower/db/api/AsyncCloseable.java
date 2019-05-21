package com.ly.train.flower.db.api;

import java.util.concurrent.CompletableFuture;
import com.ly.train.flower.db.api.support.DbCompletableFuture;

public interface AsyncCloseable {

  /**
   * Close this asyncdb resource, like connection, prepared statement etc.
   *
   * For callback style, use {@see #close()}
   */
  default CompletableFuture<Void> close() {
    DbCompletableFuture<Void> future = new DbCompletableFuture<>();
    close(future);
    return future;
  }

  /**
   * Close this asyncdb resource, like connection, prepared statement etc
   */
  void close(DbCallback<Void> callback);

}
