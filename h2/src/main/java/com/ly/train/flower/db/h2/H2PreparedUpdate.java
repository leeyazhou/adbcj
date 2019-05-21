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
package com.ly.train.flower.db.h2;

import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.PreparedUpdate;
import com.ly.train.flower.db.api.Result;


public class H2PreparedUpdate extends AbstractStatement implements PreparedUpdate {
  H2PreparedUpdate(H2Connection connection, int sessionId, int paramsCount) {
    super(connection, sessionId, paramsCount);
  }

  @Override
  public void execute(DbCallback<Result> callback, Object... params) {
    connection.checkClosed();
    if (paramsCount != params.length) {
      throw new IllegalArgumentException("Expect " + paramsCount + " parameters, but got: " + params.length);
    }
    StackTraceElement[] entry = connection.stackTraces.captureStacktraceAtEntryPoint();
    final Request request = connection.requestCreator().executeUpdateStatement(sessionId, params, callback, entry);
    connection.queRequest(request, entry);
  }
}
