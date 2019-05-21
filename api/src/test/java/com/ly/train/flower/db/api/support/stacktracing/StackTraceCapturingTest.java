package com.ly.train.flower.db.api.support.stacktracing;

import static com.ly.train.flower.db.api.support.stacktracing.StackTracingOptions.FORCED_BY_INSTANCE;
import org.testng.Assert;
import org.testng.annotations.Test;


public class StackTraceCapturingTest {
  @Test
  public void forcedCaptureOnCreation() {
    final StackTraceElement[] exeception = FORCED_BY_INSTANCE.captureStacktraceAtEntryPoint();
    Assert.assertNotNull(exeception);
  }


}
