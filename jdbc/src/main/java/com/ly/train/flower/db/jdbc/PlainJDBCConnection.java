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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;


public final class PlainJDBCConnection implements JDBCConnectionProvider {

  private static final String USER = "user";
  private static final String PASSWORD = "password";

  private final String jdbcUrl;
  private final Properties properties;

  public PlainJDBCConnection(String jdbcUrl, String username, String password, Map<String, String> properties) {
    this.jdbcUrl = jdbcUrl;
    this.properties = new Properties();
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      properties.put(entry.getKey(), entry.getValue());
    }

    this.properties.put(USER, username);
    this.properties.put(PASSWORD, password);
  }

  @Override
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(jdbcUrl, properties);

  }

  @Override
  public Connection getConnection(String user, String password) throws SQLException {
    Properties withUsername = new Properties(properties);

    withUsername.put(USER, user);
    withUsername.put(PASSWORD, password);
    return DriverManager.getConnection(jdbcUrl, withUsername);
  }
}
