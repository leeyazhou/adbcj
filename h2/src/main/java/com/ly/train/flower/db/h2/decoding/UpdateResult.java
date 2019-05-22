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
package com.ly.train.flower.db.h2.decoding;

import io.netty.channel.Channel;
import com.ly.train.flower.db.api.*;
import com.ly.train.flower.db.api.support.DefaultResultEventsHandler;
import com.ly.train.flower.db.api.support.DefaultResultSet;
import com.ly.train.flower.db.h2.H2Connection;
import com.ly.train.flower.db.h2.H2DbException;
import com.ly.train.flower.db.h2.H2Result;
import com.ly.train.flower.db.h2.protocol.StatusCodes;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class UpdateResult extends StatusReadingDecoder {
  private final DbCallback<? super Result> resultHandler;

  public UpdateResult(H2Connection connection, DbCallback<? super Result> resultHandler, StackTraceElement[] entry) {
    super(connection, entry);
    this.resultHandler = resultHandler;
  }

  @Override
  protected ResultAndState processFurther(DataInputStream stream, Channel channel, int status) throws IOException {
    StatusCodes.STATUS_OK.expectStatusOrThrow(status);

    final ResultOrWait<Integer> affected = IoUtils.tryReadNextInt(stream, ResultOrWait.Start);
    final ResultOrWait<Boolean> autoCommit = IoUtils.tryReadNextBoolean(stream, affected);
    if (autoCommit.couldReadResult) {
      DefaultResultEventsHandler<ResultSet> handler = new DefaultResultEventsHandler<>();
      DefaultResultSet result = new DefaultResultSet();

      return ResultAndState.newState(new QueryHeader<>(connection, handler, result, (success, failure) -> {
        if (failure == null) {
          H2Result updateResult = new H2Result(success, affected.result.longValue(), new ArrayList<String>());
          resultHandler.onComplete(updateResult, null);
        } else {
          resultHandler.onComplete(null, failure);
        }
      }, entry));
    } else {
      return ResultAndState.waitForMoreInput(this);
    }
  }

  @Override
  protected void requestFailedContinue(H2DbException exception) {
    resultHandler.onComplete(null, exception);
  }
}
