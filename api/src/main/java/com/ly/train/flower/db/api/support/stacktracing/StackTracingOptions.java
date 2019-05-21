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
