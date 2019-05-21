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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.support.ConnectionManagerFactory;


public class JdbcConnectionManagerFactory implements ConnectionManagerFactory {
  private static final Logger log = LoggerFactory.getLogger(JdbcConnectionManagerFactory.class);

  private static final String PROTOCOL = "jdbc";

  public ConnectionManager createConnectionManager(String url, String username, String password,
      Map<String, String> properties) throws DbException {
    try {
      URI uri = new URI(url);
      // Throw away the 'asyncdb' protocol part of the URL
      uri = new URI(uri.getSchemeSpecificPart());

      String jdbcUrl = uri.toString();

      log.warn("You are using the asyncdb to JDBC bridge, which is only intended for testing");
      log.warn("It is very slow and should not be used in production");
      log.warn("DO NOT USE IN PRODUCTION!!!");

      return new JdbcConnectionManager(new PlainJDBCConnection(jdbcUrl, username, password, properties), properties);
    } catch (URISyntaxException e) {
      throw DbException.wrap(e);
    }
  }

  @Override
  public boolean canHandle(String protocol) {
    return PROTOCOL.equals(protocol);
  }

}
