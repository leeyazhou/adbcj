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
package com.ly.train.flower.db.h2;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.support.ConnectionManagerFactory;

public class H2ConnectionManagerFactory implements ConnectionManagerFactory {
  private static final String PROTOCOL = "h2";
  private static final int DEFAULT_PORT = 8082;

  @Override
  public ConnectionManager createConnectionManager(String url, String username, String password,
      Map<String, String> properties) throws DbException {
    try {
      URI uri = new URI(url);
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
      if (schema.contains(";")) {
        schema = schema.split(";")[0];
      }
      Configuration configuration = new Configuration();
      configuration.setHost(host);
      configuration.setUsername(username);
      configuration.setPort(port);
      configuration.setPassword(password);
      configuration.setDatabase(schema);

      Map<String, String> keys = parsKeys(url);
      return new H2ConnectionManager(uri.toString(), configuration, properties, keys);

    } catch (Exception e) {
      throw DbException.wrap(e);
    }
  }

  private Map<String, String> parsKeys(String url) {
    Map<String, String> result = new HashMap<String, String>();
    final String[] keyPairString = url.split(";");
    if (keyPairString.length > 1) {
      for (int i = 1; i < keyPairString.length; i++) {
        final String[] keyValue = keyPairString[i].split("=");
        result.put(keyValue[0], keyValue[1]);
      }
    }
    return result;
  }

  @Override
  public boolean canHandle(String protocol) {
    return PROTOCOL.equalsIgnoreCase(protocol);
  }
}
