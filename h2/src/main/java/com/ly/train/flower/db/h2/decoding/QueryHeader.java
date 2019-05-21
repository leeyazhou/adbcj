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
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.ResultHandler;
import com.ly.train.flower.db.api.support.SizeConstants;
import com.ly.train.flower.db.h2.H2Connection;
import com.ly.train.flower.db.h2.H2DbException;
import com.ly.train.flower.db.h2.protocol.StatusCodes;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class QueryHeader<T> extends StatusReadingDecoder {
  private final ResultHandler<T> eventHandler;
  private final T accumulator;
  private final DbCallback<T> callback;
  private DbException failure;

  public QueryHeader(H2Connection connection, ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback,
      StackTraceElement[] entry) {
    super(connection, entry);
    this.eventHandler = eventHandler;
    this.accumulator = accumulator;
    this.callback = callback;
  }

  @Override
  protected ResultAndState processFurther(final DataInputStream stream, Channel channel, int status)
      throws IOException {
    StatusCodes.STATUS_OK.expectStatusOrThrow(status);

    if (stream.available() < (SizeConstants.INT_SIZE + SizeConstants.INT_SIZE)) {
      return ResultAndState.waitForMoreInput(this);
    } else {
      int columnCount = stream.readInt();
      int rowCount = stream.readInt();
      try {
        eventHandler.startFields(accumulator);
      } catch (Exception any) {
        failure = DbException.wrap(any, entry);
      }
      return ResultAndState.newState(new ColumnDecoder<T>(connection, eventHandler, accumulator, failure, callback,
          entry, rowCount, columnCount, new ArrayList<>()));
    }

  }

  @Override
  protected void requestFailedContinue(H2DbException exception) {
    callback.onComplete(null, exception);
  }
}

