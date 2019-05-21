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

import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.packets.Command;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public class StringCommandRequest extends CommandRequest {
  private final String payload;
  private byte[] dataAsBytes = null;

  public StringCommandRequest(Command command, String payload) {
    super(command);
    this.payload = payload;
  }


  @Override
  public int getLength() {
    return 1 + payloadAsBinary().length;
  }

  @Override
  protected boolean hasPayload() {
    return payload != null;
  }

  @Override
  protected void writePayLoad(OutputStream out) throws IOException {
    out.write(payloadAsBinary());
  }

  @Override
  public String toString() {
    return "StringCommandRequest{" + "payload='" + payload + '\'' + '}';
  }

  private byte[] payloadAsBinary() {
    if (null != dataAsBytes) {
      return dataAsBytes;
    }
    try {
      dataAsBytes = payload.getBytes(MysqlCharacterSet.UTF8_UNICODE_CI.getCharsetName());
      return dataAsBytes;
    } catch (UnsupportedEncodingException e) {
      throw new DbException(e.getMessage(), e);
    }
  }


}
