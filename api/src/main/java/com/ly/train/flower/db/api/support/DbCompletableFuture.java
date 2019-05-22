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
package com.ly.train.flower.db.api.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.exception.DbException;
import java.util.concurrent.CompletableFuture;

public final class DbCompletableFuture<T> extends CompletableFuture<T> implements DbCallback<T> {
  private static final Logger logger = LoggerFactory.getLogger(DbCompletableFuture.class);

  @Override
  public void onComplete(T result, DbException failure) {
    if (failure == null) {
      if (!complete(result)) {
        if (logger.isWarnEnabled()) {
          logger.warn("Tried to complete a already completed future. Tried to complete as success with value {}",
              result);
        }
      }
    } else {
      if (!this.completeExceptionally(failure)) {
        if (logger.isWarnEnabled()) {
          logger.warn("Tried to fail a already completed future. Tried to signal failure {}", failure);
        }
      }
    }
  }

}
