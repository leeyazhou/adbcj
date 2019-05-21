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

import java.util.ArrayList;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.support.OneArgFunction;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.MysqlResult;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.OKRegularResponse;


public class ExpectUpdateResultDecoder<T> extends OKResponseDecoder<T> {

  private final OneArgFunction<MysqlResult, T> transformation;

  public ExpectUpdateResultDecoder(MySqlConnection connection, DbCallback<T> callback, StackTraceElement[] entry) {
    this(connection, callback, entry, OneArgFunction.ID_FUNCTION);
  }

  public ExpectUpdateResultDecoder(MySqlConnection connection, DbCallback<T> callback, StackTraceElement[] entry,
      OneArgFunction<MysqlResult, T> transformation) {
    super(connection, callback, entry);
    this.transformation = transformation;
  }

  @Override
  protected ResponseWrapper handleOk(OKRegularResponse oKRegularResponse) {
    return handleUpdateResult(connection, oKRegularResponse, callback, transformation);
  }

  static <TFutureType> ResponseWrapper handleUpdateResult(MySqlConnection connection, OKRegularResponse oKRegularResponse,
      DbCallback<TFutureType> futureToComplete, OneArgFunction<MysqlResult, TFutureType> transformation) {
    ArrayList<String> warnings = new ArrayList<String>(oKRegularResponse.getWarningCount());
    for (int i = 0; i < oKRegularResponse.getWarningCount(); i++) {
      warnings.add(oKRegularResponse.getMessage());
    }
    MysqlResult result = new MysqlResult(oKRegularResponse.getAffectedRows(), warnings, oKRegularResponse.getInsertId());
    futureToComplete.onComplete(transformation.apply(result), null);
    return new ResponseWrapper(new AcceptNextResponseDecoder(connection), oKRegularResponse);
  }
}
