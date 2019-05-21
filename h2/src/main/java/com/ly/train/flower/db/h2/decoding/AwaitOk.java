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
import java.io.DataInputStream;
import java.io.IOException;
import com.ly.train.flower.db.h2.H2Connection;
import com.ly.train.flower.db.h2.H2DbException;


public class AwaitOk extends StatusReadingDecoder {
  public AwaitOk(H2Connection connection, StackTraceElement[] entry) {
    super(connection, entry);
  }

  @Override
  protected ResultAndState processFurther(DataInputStream stream, Channel channel, int status) throws IOException {
    return ResultAndState.newState(new AnswerNextRequest(connection, entry));
  }

  @Override
  protected void requestFailedContinue(H2DbException exception) {
    throw exception;
  }
}

