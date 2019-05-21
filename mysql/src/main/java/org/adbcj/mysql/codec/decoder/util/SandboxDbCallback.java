package org.adbcj.mysql.codec.decoder.util;

import org.adbcj.DbCallback;
import org.adbcj.DbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
