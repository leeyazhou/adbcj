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
package com.ly.train.flower.db.tck;

import org.h2.tools.Server;
import com.ly.train.flower.db.jdbc.PlainJDBCConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;


public class InitH2 extends InitDatabase {

  @Override
  protected String setupScript() {
    return "sql/setupH2.sql";
  }

  @Override
  protected String tearDownScript() {
    return "sql/cleanUpH2.sql";
  }

  @Override
  protected void beforeSetupScript(String jdbcUrl, String user, String password) {

    try {
      Connection connection = new PlainJDBCConnection(jdbcUrl, user, password, new HashMap<>()).getConnection();
      if (null != connection) {
        connection.close();
        return;
      }
    } catch (SQLException e) {
      // expected, server not running
    }
    try {
      Server server =
          Server.createTcpServer("-tcpAllowOthers", "-tcpDaemon", "-tcpPort", "14242", "-baseDir", "./h2testdb");
      // Server inspect = Server.createWebServer();
      // inspect.start();
      server.start();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  protected void afterCleanupScript() {}
}
