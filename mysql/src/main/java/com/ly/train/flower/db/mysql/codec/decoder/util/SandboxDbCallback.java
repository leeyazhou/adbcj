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
package com.ly.train.flower.db.mysql.codec.decoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.exception.DbException;

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
