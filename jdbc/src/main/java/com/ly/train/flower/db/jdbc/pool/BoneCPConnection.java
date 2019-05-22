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
package com.ly.train.flower.db.jdbc.pool;

import com.jolbox.bonecp.BoneCP;
import com.ly.train.flower.db.jdbc.JDBCConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;


public class BoneCPConnection implements JDBCConnectionProvider {
  private final BoneCP connectionPool;

  public BoneCPConnection(BoneCP connectionPool) {
    this.connectionPool = connectionPool;
  }


  @Override
  public Connection getConnection() throws SQLException {
    return connectionPool.getConnection();
  }

  @Override
  public Connection getConnection(String user, String password) throws SQLException {
    throw new UnsupportedOperationException("Not yet supported for JDBC connection pool");
  }
}
