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
import java.util.List;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.exception.DbException;
import com.ly.train.flower.db.api.handler.ResultHandler;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.MysqlField;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.EofResponse;
import io.netty.channel.Channel;


class FieldEofDecoder<T> extends AbstractDecoder {
  private final List<MysqlField> fields;
  private final MySqlConnection connection;
  private final ResultHandler<T> eventHandler;
  private final T accumulator;
  private final DbCallback<T> callback;
  private final StackTraceElement[] entry;
  private RowDecoder.RowDecodingType decodingType;
  private DbException failure;

  public FieldEofDecoder(MySqlConnection connection, RowDecoder.RowDecodingType decodingType, List<MysqlField> fields,
      ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback, StackTraceElement[] entry,
      DbException failure) {
    this.decodingType = decodingType;
    this.fields = fields;
    this.callback = sandboxCallback(callback);
    this.connection = connection;
    this.eventHandler = eventHandler;
    this.accumulator = accumulator;
    this.entry = entry;
    this.failure = failure;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel) throws IOException {
    int fieldCount = in.read();

    try {
      eventHandler.endFields(accumulator);
      eventHandler.startResults(accumulator);
    } catch (Exception ex) {
      this.failure = DbException.attachSuppressedOrWrap(ex, entry, failure);
    }

    if (fieldCount != RESPONSE_EOF) {
      throw new IllegalStateException("Expected an EOF response from the server");
    }
    EofResponse fieldEof = decodeEofResponse(in, length, packetNumber, EofResponse.Type.FIELD);
    return resultWrapper(new RowDecoder<T>(connection, decodingType, fields, eventHandler, accumulator, callback, entry, failure),
        fieldEof);
  }

  @Override
  public String toString() {
    return "FIELD-EOF";
  }


}
