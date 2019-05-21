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

import java.util.ArrayList;
import java.util.List;
import com.ly.train.flower.db.mysql.codec.model.MysqlType;


public final class PreparedStatementResponse extends AbstractResponse {
  private final OKPreparedStatementResponse preparedStatement;
  private final List<MysqlType> parametersTypes;

  public PreparedStatementResponse(int packetLength, int packetNumber,
      OKPreparedStatementResponse preparedStatement) {
    this(packetLength, packetNumber, preparedStatement, new ArrayList<MysqlType>());
  }

  public PreparedStatementResponse(int packetLength, int packetNumber,
      OKPreparedStatementResponse preparedStatement, List<MysqlType> parametersTypes) {
    super(packetLength, packetNumber);
    this.preparedStatement = preparedStatement;
    this.parametersTypes = parametersTypes;
  }

  public OKPreparedStatementResponse getPreparedStatement() {
    return preparedStatement;
  }

  public int getHandlerId() {
    return preparedStatement.getHandlerId();
  }

  public List<MysqlType> getParametersTypes() {
    return parametersTypes;
  }

  public int getColumns() {
    return preparedStatement.getColumns();
  }

  public int getParams() {
    return preparedStatement.getParams();
  }
}
