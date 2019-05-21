package com.ly.train.flower.db.mysql.codec.decoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbException;

public class SandboxDbCallback<T> implements DbCallback<T> {
  private static final Logger logger = LoggerFactory.getLogger(SandboxDbCallback.class);
  private final DbCallback<T> callback;

  public SandboxDbCallback(final DbCallback<T> callback) {
    this.callback = callback;
  }

  @Override
  public void onComplete(final T result, final DbException failure) {
    try {
      callback.onComplete(result, failure);
    } catch (final Throwable cause) {
      logger.warn("Uncaught exception in DbCallback", cause);
    }
  }

}
