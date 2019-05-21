package com.ly.train.flower.db.api;

public interface PreparedStatement extends AsyncCloseable {

  boolean isClosed();


  void close(DbCallback<Void> callback);
}
