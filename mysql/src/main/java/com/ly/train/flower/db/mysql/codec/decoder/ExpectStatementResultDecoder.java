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

import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.ResultHandler;
import com.ly.train.flower.db.api.support.OneArgFunction;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.OKRegularResponse;


public class ExpectStatementResultDecoder<T> extends ExpectQueryResultDecoder<T> {

  public ExpectStatementResultDecoder(MySqlConnection connection, RowDecoder.RowDecodingType decodingType,
      ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback, StackTraceElement[] entry) {
    super(connection, decodingType, eventHandler, accumulator, callback, entry);
  }


  @Override
  protected ResponseWrapper handleOk(OKRegularResponse oKRegularResponse) {
    return ExpectUpdateResultDecoder.handleUpdateResult(connection, oKRegularResponse, callback, OneArgFunction.ID_FUNCTION);
  }
}
