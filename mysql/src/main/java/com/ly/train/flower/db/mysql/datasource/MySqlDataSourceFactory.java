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
package com.ly.train.flower.db.mysql.datasource;

import java.net.URI;
import java.net.URISyntaxException;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.datasource.DataSource;
import com.ly.train.flower.db.api.datasource.DataSourceFactory;
import com.ly.train.flower.db.api.exception.DbException;

public class MySqlDataSourceFactory implements DataSourceFactory {

  public static final String PROTOCOL = "mysql";
  public static final int DEFAULT_PORT = 3306;

  @Override
  public DataSource createDataSource(Configuration configuration) throws DbException {
    try {
      URI uri = new URI(configuration.getUrl());
      uri = new URI(uri.getSchemeSpecificPart());
      String host = uri.getHost();
      int port = uri.getPort();
      if (port < 0) {
        port = DEFAULT_PORT;
      }
      String path = uri.getPath().trim();
      if (path.length() == 0 || "/".equals(path)) {
        throw new DbException("You must specific a database in the URL path");
      }
      String schema = path.substring(1);

      configuration.setDatabase(schema);
      configuration.setHost(host);
      configuration.setPort(port);

      return new MysqlDataSource(configuration);
    } catch (URISyntaxException e) {
      throw new DbException("Could not create connection to " + configuration.getUrl(), e);
    }
  }

  @Override
  public boolean canHandle(String protocol) {
    return PROTOCOL.equals(protocol);
  }

}
