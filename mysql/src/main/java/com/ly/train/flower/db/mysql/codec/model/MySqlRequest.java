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
package com.ly.train.flower.db.mysql.codec.model;

import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.mysql.codec.decoder.AbstractDecoder;
import com.ly.train.flower.db.mysql.codec.packets.request.AbstractRequest;


public class MySqlRequest<T> {
  private final String description;
  private final AbstractDecoder decoder;
  private final AbstractRequest request;
  private final DbCallback<T> callback;

  public MySqlRequest(String description, AbstractDecoder decoder, AbstractRequest request, DbCallback<T> callback) {
    this.description = description;
    this.decoder = decoder;
    this.request = request;
    this.callback = callback;
  }

  @Override
  public String toString() {
    return "MySqlRequest{" + description + '}';
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the startState
   */
  public AbstractDecoder getDecoder() {
    return decoder;
  }

  /**
   * @return the request
   */
  public AbstractRequest getRequest() {
    return request;
  }

  /**
   * @return the callback
   */
  public DbCallback<T> getCallback() {
    return callback;
  }
}
