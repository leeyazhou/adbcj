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
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.MySqlPreparedStatement;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.ErrorResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.OKPreparedStatementResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.OkResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.PreparedStatementResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.StatementPreparedEOFResponse;
import io.netty.channel.Channel;


public class ExpectPreparQueryDecoder<T> extends AbstractDecoder {
  private final MySqlConnection connection;
  private final DbCallback<MySqlPreparedStatement> callback;
  private final StackTraceElement[] entry;

  public ExpectPreparQueryDecoder(MySqlConnection connection, DbCallback<MySqlPreparedStatement> callback,
      StackTraceElement[] entry) {
    this.callback = sandboxCallback(callback);
    this.entry = entry;
    this.connection = connection;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
      throws IOException {
    int fieldCount = in.read();
    if (fieldCount == AbstractResponseDecoder.RESPONSE_OK) {
      return handlePrepareQuery(length, packetNumber,
          OkResponse.interpretAsPreparedStatement(length, packetNumber, in));
    }
    if (fieldCount == AbstractResponseDecoder.RESPONSE_ERROR) {
      return handleError(decodeErrorResponse(in, length, packetNumber));
    } else {
      throw new IllegalStateException("Did not expect this response from the server");
    }
  }

  private ResponseWrapper handlePrepareQuery(int length, int packetNumber,
      OKPreparedStatementResponse preparedStatement) {
    final PreparedStatementResponse statement = new PreparedStatementResponse(length, packetNumber, preparedStatement);
    final AbstractDecoder decoder = FinishPrepareStatementDecoder.create(connection, statement, callback);
    if (decoder instanceof AcceptNextResponseDecoder) {
      final StatementPreparedEOFResponse eof = new StatementPreparedEOFResponse(length, packetNumber, statement);
      callback.onComplete(new MySqlPreparedStatement(connection, eof), null);
      return new ResponseWrapper(decoder, eof);
    } else {
      return new ResponseWrapper(decoder, preparedStatement);
    }
  }

  private ResponseWrapper handleError(ErrorResponse errorResponse) {
    callback.onComplete(null, errorResponse.toException(entry));
    return new ResponseWrapper(new AcceptNextResponseDecoder(connection), errorResponse);
  }
}
