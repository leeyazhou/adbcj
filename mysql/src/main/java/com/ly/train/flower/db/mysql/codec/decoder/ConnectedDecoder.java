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

import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.exception.DbException;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.ErrorResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.OKRegularResponse;

/**
 * 连接成功解码器
 * 
 * @author lee
 */
public class ConnectedDecoder extends AbstractResponseDecoder {
  private final DbCallback<Connection> connected;
  private final StackTraceElement[] entry;

  public ConnectedDecoder(DbCallback<Connection> callback, StackTraceElement[] entry, MySqlConnection connection) {
    super(connection);
    this.connected = sandboxCallback(callback);
    this.entry = entry;
  }

  @Override
  protected ResponseWrapper handleError(ErrorResponse errorResponse) {
    connected.onComplete(null, DbException.wrap(errorResponse.toException(entry), entry));
    return new ResponseWrapper(acceptNextResponseDecoder(), errorResponse);
  }

  @Override
  protected ResponseWrapper handleOk(OKRegularResponse oKRegularResponse) {
    connected.onComplete(connection, null);
    return new ResponseWrapper(acceptNextResponseDecoder(), oKRegularResponse);
  }

  protected AcceptNextResponseDecoder acceptNextResponseDecoder() {
    return new AcceptNextResponseDecoder(connection);
  }
}
