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

import java.util.concurrent.CompletableFuture;
import com.ly.train.flower.db.api.support.DbCompletableFuture;

public interface PreparedUpdate extends PreparedStatement {

  default CompletableFuture<Result> execute(Object... params) {
    DbCompletableFuture<Result> future = new DbCompletableFuture<>();
    execute(future, params);
    return future;
  }

  void execute(DbCallback<Result> callback, Object... params);
}
