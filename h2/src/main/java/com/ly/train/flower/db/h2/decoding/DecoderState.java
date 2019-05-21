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
import com.ly.train.flower.db.h2.H2DbException;


public interface DecoderState {
  /**
   * Decodes the stream according to it's state.
   *
   * Returns the new state and the parsed object. If not all data is available
   * yet, no object will be returned.
   * 
   * @param stream the data to read
   * @return state
   */
  ResultAndState decode(DataInputStream stream, Channel channel) throws IOException;

  /**
   * Handle the exception occurred for this decoding staten
   *
   * Returns the new state
   * 
   * @return state
   */
  ResultAndState handleException(H2DbException exception);



}

