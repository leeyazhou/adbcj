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
import com.ly.train.flower.db.api.PreparedStatement;
import com.ly.train.flower.db.api.support.CloseOnce;


public class AbstractStatement implements PreparedStatement {
  protected final H2Connection connection;
  protected final int sessionId;
  protected final int paramsCount;
  private final CloseOnce closer = new CloseOnce();

  public AbstractStatement(H2Connection connection, int sessionId, int paramsCount) {
    this.paramsCount = paramsCount;
    this.connection = connection;
    this.sessionId = sessionId;
  }

  @Override
  public boolean isClosed() {
    return closer.isClose();
  }

  @Override
  public void close(DbCallback<Void> callback) {
    synchronized (connection.connectionLock()) {
      StackTraceElement[] entry = connection.stackTraces.captureStacktraceAtEntryPoint();
      closer.requestClose(callback, () -> {
        Request<Void> req =
            connection.requestCreator().executeCloseStatement(sessionId, (res, error) -> closer.didClose(error), entry);
        connection.forceQueRequest(req);
      });
    }
  }
}
