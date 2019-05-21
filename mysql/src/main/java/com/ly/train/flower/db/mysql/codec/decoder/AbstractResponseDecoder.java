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
package com.ly.train.flower.db.mysql.codec.decoder;

import java.io.IOException;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.ErrorResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.OKRegularResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.OkResponse;
import io.netty.channel.Channel;

public abstract class AbstractResponseDecoder extends AbstractDecoder {

  protected final MySqlConnection connection;

  protected AbstractResponseDecoder(MySqlConnection connection) {
    this.connection = connection;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
      throws IOException {
    int fieldCount = in.read();
    if (fieldCount == RESPONSE_OK) {
      return handleOk(OkResponse.interpretAsRegularOk(length, packetNumber, in));
    }
    if (fieldCount == RESPONSE_ERROR) {
      return handleError(decodeErrorResponse(in, length, packetNumber));
    }
    if (fieldCount == RESPONSE_EOF) {
      throw new IllegalStateException("Did not expect an EOF response from the server");
    }
    return parseAsResult(length, packetNumber, in, fieldCount);
  }

  protected abstract ResponseWrapper handleError(ErrorResponse errorResponse);

  protected abstract ResponseWrapper handleOk(OKRegularResponse oKRegularResponse);

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }

  protected ResponseWrapper parseAsResult(int length, int packetNumber, BoundedInputStream in, int fieldCount)
      throws IOException {
    throw new IllegalStateException(
        "This state: " + this + " does not expect a result which can be interpreted as " + "query result");
  }

}
