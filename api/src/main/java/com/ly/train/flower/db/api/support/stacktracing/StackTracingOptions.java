package com.ly.train.flower.db.api.support.stacktracing;

import com.ly.train.flower.db.api.support.ConnectionManagerFactory;

public enum StackTracingOptions {
  /**
   * Only trace when the JVM "org.asyncdb.debug" flag is set to true.
   * <p>
   * Activate tracing at start up with it with -Dorg.asyncdb.debug=true
   */
  GLOBAL_DEFAULT {
    private final boolean isOn = Boolean.getBoolean("org.asyncdb.debug");

    @Override
    public StackTraceElement[] captureStacktraceAtEntryPoint() {
      if (isOn) {
        return Thread.currentThread().getStackTrace();
      } else {
        return null;
      }
    }

  },
  /**
   * This {@link ConnectionManagerFactory} or connection wants to have a
   * stack-trace captured, no mather what.
   */
  FORCED_BY_INSTANCE {
    @Override
    public StackTraceElement[] captureStacktraceAtEntryPoint() {
      return Thread.currentThread().getStackTrace();
    }
  };

  public abstract StackTraceElement[] captureStacktraceAtEntryPoint();
}
