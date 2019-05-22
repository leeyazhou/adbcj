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
import com.ly.train.flower.db.api.handler.ResultHandler;
import com.ly.train.flower.db.api.support.DbCompletableFuture;
import com.ly.train.flower.db.api.support.DefaultResultEventsHandler;
import com.ly.train.flower.db.api.support.DefaultResultSet;

public interface PreparedQuery extends PreparedStatement {

  @SuppressWarnings({"unchecked", "rawtypes"})
  default CompletableFuture<ResultSet> execute(Object... params) {
    DefaultResultEventsHandler handler = new DefaultResultEventsHandler();
    DefaultResultSet acc = new DefaultResultSet();
    DbCompletableFuture<DefaultResultSet> future = new DbCompletableFuture<>();
    executeWithCallback(handler, acc, future, params);
    return (CompletableFuture) future;
  }

  default <T> CompletableFuture<T> executeWithCallback(ResultHandler<T> handler, T accumulator, Object... params) {
    DbCompletableFuture<T> future = new DbCompletableFuture<>();
    executeWithCallback(handler, accumulator, future, params);
    return future;
  }

  <T> void executeWithCallback(ResultHandler<T> handler, T accumulator, DbCallback<T> callback, Object... params);
}
