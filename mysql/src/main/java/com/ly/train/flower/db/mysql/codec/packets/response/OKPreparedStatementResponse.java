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
package com.ly.train.flower.db.mysql.codec.packets.response;

public class OKPreparedStatementResponse extends OkResponse {

  private final int handlerId;
  private final int columns;
  private final int params;
  private final int warnings;

  public OKPreparedStatementResponse(int packetLength, int packetNumber, int handlerId, int columns, int params,
      int warnings) {
    super(packetLength, packetNumber);
    this.handlerId = handlerId;
    this.columns = columns;
    this.params = params;
    this.warnings = warnings;
  }

  public int getHandlerId() {
    return handlerId;
  }

  public int getColumns() {
    return columns;
  }

  public int getParams() {
    return params;
  }

  public int getWarnings() {
    return warnings;
  }
}
