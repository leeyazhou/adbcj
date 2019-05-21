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


public enum CloseMode {
  /**
   * Closes when pending operations have completed. All enqueued requests will be
   * finished, before the resource is closed.
   *
   * However, no new requests / commands will be accepted.
   */
  CLOSE_GRACEFULLY,
  /**
   * Cancels all pending requests and the closes the resource. No new requests /
   * commands will be accepted.
   */
  CANCEL_PENDING_OPERATIONS,
  /**
   * Cancels all pending requests and then closes the resource forcibly even if
   * using connection pool. No new requests / commands will be accepted.
   * 
   * @since 2017-09-02 little-pan
   */
  CLOSE_FORCIBLY;
}
