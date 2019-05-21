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

import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.h2.H2Connection;
import com.ly.train.flower.db.h2.H2DbException;
import com.ly.train.flower.db.h2.protocol.StatusCodes;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import java.io.DataInputStream;
import java.io.IOException;


public class CloseConnection extends StatusReadingDecoder {
  private final DbCallback<Void> finishedClose;

  public CloseConnection(H2Connection connection, DbCallback<Void> finishedClose, StackTraceElement[] entry) {
    super(connection, entry);
    this.finishedClose = finishedClose;
  }

  @Override
  protected ResultAndState processFurther(DataInputStream stream, Channel channel, int status) throws IOException {
    StatusCodes.STATUS_OK.expectStatusOrThrow(status);
    channel.close().addListener((ChannelFutureListener) future -> finishedClose.onComplete(null, null));
    return ResultAndState.newState(new ClosedConnectionState(finishedClose, connection, entry));
  }

  @Override
  protected void requestFailedContinue(H2DbException exception) {
    finishedClose.onComplete(null, exception);
  }
}
