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
 * The core callback for all operations in asyncdb. A operaition in asyncdb ends in
 * a call of {@see #onComplete}. On a success, the result is passed, and the
 * failure null. A result can be null. On a failure, the result is null and
 * failure an exception.
 *
 * The callback is called on an thread of the asyncdb drivers thread pool. You
 * most not do any blocking operations in the callback. You can issue new
 * operations on the {@see org.asyncdb.Connection} in the callback.
 *
 * @param <T>
 */
public interface DbCallback<T> {

  /**
   * Called when a asyncdb operation completes. On a success, the result is passed,
   * and the failure null. A result can be null. On a failure, the result is null
   * and failure an exception.
   * 
   * @param result if the {@param failure} is null, it contains the result. Some
   *        operaitions may return null as result
   * @param failure if not null, a failure occured. The exception contains more
   *        information
   */
  void onComplete(T result, DbException failure);
}
