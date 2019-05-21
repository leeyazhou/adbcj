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
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.support.SizeConstants;
import com.ly.train.flower.db.h2.H2Connection;
import com.ly.train.flower.db.h2.H2DbException;
import com.ly.train.flower.db.h2.protocol.StatusCodes;
import java.io.DataInputStream;
import java.io.IOException;


public abstract class StatementPrepare<T> extends StatusReadingDecoder {
  protected final DbCallback<T> callback;

  public StatementPrepare(DbCallback<T> callback, H2Connection connection, StackTraceElement[] entry) {
    super(connection, entry);
    this.callback = callback;
  }

  @Override
  protected ResultAndState processFurther(DataInputStream stream, Channel channel, int status) throws IOException {
    StatusCodes.STATUS_OK.expectStatusOrThrow(status);
    if (stream.available() >= (SizeConstants.BYTE_SIZE + SizeConstants.BYTE_SIZE + SizeConstants.INT_SIZE)) {
      boolean isQuery = IoUtils.readBoolean(stream);
      boolean readonly = IoUtils.readBoolean(stream);
      int paramsCount = stream.readInt();
      handleCompletion(connection, paramsCount);
      return ResultAndState.newState(new AnswerNextRequest(connection, entry));
    } else {
      return ResultAndState.waitForMoreInput(this);
    }
  }

  @Override
  public ResultAndState handleException(H2DbException exception) {
    super.handleException(exception);
    return ResultAndState.newState(new AnswerNextRequest(connection, entry));
  }

  protected abstract void handleCompletion(H2Connection connection, int paramsCount);


  @Override
  protected void requestFailedContinue(H2DbException exception) {
    callback.onComplete(null, exception);
  }

  public static StatementPrepare<Connection> createOnlyPassFailure(final DbCallback<Connection> resultFuture,
      final H2Connection connection, StackTraceElement[] entry) {
    return new StatementPrepare<Connection>(resultFuture, connection, entry) {
      @Override
      protected void handleCompletion(H2Connection connection, int paramsCount) {}
    };
  }

  public static StatementPrepare<Connection> completeFuture(final DbCallback<Connection> resultFuture,
      final H2Connection connection, StackTraceElement[] entry) {
    return new StatementPrepare<Connection>(resultFuture, connection, entry) {
      @Override
      protected void handleCompletion(H2Connection connection, int paramsCount) {
        resultFuture.onComplete(connection, null);
      }
    };
  }
}
