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
import com.ly.train.flower.db.api.support.SizeConstants;
import com.ly.train.flower.db.h2.H2Connection;
import com.ly.train.flower.db.h2.H2DbException;
import com.ly.train.flower.db.h2.protocol.StatusCodes;
import java.io.DataInputStream;
import java.io.IOException;


public abstract class StatusReadingDecoder implements DecoderState {
  protected final H2Connection connection;
  protected final StackTraceElement[] entry;

  protected StatusReadingDecoder(H2Connection connection, StackTraceElement[] entry) {
    this.connection = connection;
    this.entry = entry;
  }

  public final ResultAndState decode(DataInputStream stream, Channel channel) throws IOException {
    if (stream.available() < SizeConstants.INT_SIZE) {
      return ResultAndState.waitForMoreInput(this);
    }
    final int status = stream.readInt();
    if (StatusCodes.STATUS_ERROR.isStatus(status)) {
      ResultOrWait<String> sqlstate = IoUtils.tryReadNextString(stream, ResultOrWait.Start);
      ResultOrWait<String> message = IoUtils.tryReadNextString(stream, sqlstate);
      ResultOrWait<String> sql = IoUtils.tryReadNextString(stream, message);
      ResultOrWait<Integer> errorCode = IoUtils.tryReadNextInt(stream, sql);
      ResultOrWait<String> stackTrace = IoUtils.tryReadNextString(stream, errorCode);
      if (stackTrace.couldReadResult) {
        return handleException(H2DbException.create(sqlstate.result, message.result, sql.result, errorCode.result,
            stackTrace.result, entry));
      } else {
        return continueWithNextRequest();
      }
    } else {
      return processFurther(stream, channel, status);
    }
  }

  private ResultAndState continueWithNextRequest() {
    return ResultAndState.waitForMoreInput(this);
  }

  protected abstract ResultAndState processFurther(DataInputStream stream, Channel channel, int status)
      throws IOException;

  @Override
  public ResultAndState handleException(H2DbException exception) {
    requestFailedContinue(exception);
    return ResultAndState.newState(new AnswerNextRequest(connection, entry));
  }

  protected void requestFailedContinue(H2DbException exception) {}


}
