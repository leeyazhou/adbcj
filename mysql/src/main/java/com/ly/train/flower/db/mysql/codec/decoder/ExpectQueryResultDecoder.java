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
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.exception.DbException;
import com.ly.train.flower.db.api.handler.ResultHandler;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.ErrorResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.OKRegularResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.ResultSetResponse;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;


public class ExpectQueryResultDecoder<T> extends AbstractResponseDecoder {
  private static final Logger logger = LoggerFactory.getLogger(ExpectQueryResultDecoder.class);
  private final ResultHandler<T> eventHandler;
  private final T accumulator;
  protected final DbCallback<T> callback;
  private RowDecoder.RowDecodingType decodingType;
  private final StackTraceElement[] entry;
  private DbException failure;

  public ExpectQueryResultDecoder(MySqlConnection connection, RowDecoder.RowDecodingType decodingType,
      ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback, StackTraceElement[] entry) {
    super(connection);
    this.decodingType = decodingType;
    this.eventHandler = eventHandler;
    this.accumulator = accumulator;
    this.callback = sandboxCallback(callback);
    this.entry = entry;
  }

  @Override
  protected ResponseWrapper handleError(ErrorResponse errorResponse) {
    callback.onComplete(null, errorResponse.toException(entry));
    return new ResponseWrapper(new AcceptNextResponseDecoder(connection), errorResponse);
  }

  @Override
  protected ResponseWrapper handleOk(OKRegularResponse oKRegularResponse) {
    throw new Error("Not supported for query results");
  }

  @Override
  protected ResponseWrapper parseAsResult(int length, int packetNumber, BoundedInputStream in, int fieldCount)
      throws IOException {
    // Get the number of fields. The largest this can be is a 24-bit
    // integer so cast to int is ok
    int expectedFieldPackets = (int) IOUtil.readBinaryLengthEncoding(in, fieldCount);
    logger.trace("Field count {}", expectedFieldPackets);

    Long extra = null;
    if (in.getRemaining() > 0) {
      extra = IOUtil.readBinaryLengthEncoding(in);
    }
    try {
      eventHandler.startFields(accumulator);
    } catch (Exception any) {
      failure = DbException.wrap(any, entry);
    }
    AbstractDecoder decoder = new FieldDecodingStateDecoder<T>(connection, decodingType, expectedFieldPackets,
        new ArrayList<>(), eventHandler, accumulator, callback, entry, failure);
    return resultWrapper(decoder, new ResultSetResponse(length, packetNumber, expectedFieldPackets, extra));
  }
}
