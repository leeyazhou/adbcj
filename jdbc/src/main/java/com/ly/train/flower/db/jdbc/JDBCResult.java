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
package com.ly.train.flower.db.jdbc;

import java.sql.SQLException;
import java.util.List;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.support.DefaultResult;
import com.ly.train.flower.db.api.support.DefaultResultEventsHandler;
import com.ly.train.flower.db.api.support.DefaultResultSet;


class JDBCResult extends DefaultResult {
  private final ResultSet generatedKeys;

  public JDBCResult(long affectedRows, List<String> warnings, java.sql.ResultSet generatedKeys,
      DbCallback<DefaultResultSet> callback, StackTraceElement[] entry) {
    super(affectedRows, warnings);
    DefaultResultSet resultSet = new DefaultResultSet();
    try {
      ResultSetCopier.fillResultSet(generatedKeys, new DefaultResultEventsHandler(), resultSet);
      this.generatedKeys = resultSet;
    } catch (SQLException e) {
      throw DbException.wrap(e);
    }
  }

  @Override
  public ResultSet getGeneratedKeys() {
    return generatedKeys;
  }
}
