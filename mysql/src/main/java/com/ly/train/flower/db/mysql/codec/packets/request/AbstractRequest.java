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
package com.ly.train.flower.db.mysql.codec.packets.request;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public abstract class AbstractRequest {

  protected AbstractRequest() {}


  public abstract int getLength() throws UnsupportedEncodingException;

  /**
   * The packet number is sent as a byte so only the least significant byte will
   * be used.
   *
   */
  public int getPacketNumber() {
    return 0;
  }

  protected abstract boolean hasPayload();

  public abstract void writeToOutputStream(OutputStream out) throws IOException;


}
