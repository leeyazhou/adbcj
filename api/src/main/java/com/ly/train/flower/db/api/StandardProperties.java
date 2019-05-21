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
package com.ly.train.flower.db.api;

/**
 * Standard properties, which can be passed to
 * {@link ConnectionManagerProvider#createConnectionManager} via property map
 */
public final class StandardProperties {
  private StandardProperties() {}

  /**
   * asyncdb allows to have multiple async operations open running. This parameter
   * limits how many operations can be open before the implementation throws. It
   * is a per connection setting.
   *
   * This prevents the situation where a connection or system gets over whelmed
   * with open requests. By default this is 64.
   */
  public final static String MAX_QUEUE_LENGTH = "asyncdb.maxQueueLength";

  public final static long DEFAULT_QUEUE_LENGTH = 64;

  /**
   * asyncdb allows you to capture the stack trace of the location which issues a
   * request. However this is a expensive operation, so it's optional. You can
   * force a driver to capture this stack by setting this option.
   *
   * If not set, the JVM global property 'org.asyncdb.debug' set to true will force
   * capturing stack trace.
   *
   * This is disabled by default
   */
  public final static String CAPTURE_CALL_STACK = "org.asyncdb.debug.capture.callstack";


  /**
   * When set to true, enables the default confection pool. If a driver does not
   * support a connection pool, it should throw a {@link DbException}.
   *
   *
   */
  public final static String CONNECTION_POOL_ENABLE = "org.asyncdb.connectionpool.enable";
}
