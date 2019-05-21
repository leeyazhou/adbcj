package com.ly.train.flower.db.mysql;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.support.ConnectionManagerFactory;

public class MySqlConnectionManagerFactory implements ConnectionManagerFactory {

  public static final String PROTOCOL = "mysql";
  public static final int DEFAULT_PORT = 3306;

  @Override
  public ConnectionManager createConnectionManager(String url, String username, String password,
      Map<String, String> properties) throws DbException {
    try {
      /*
       * Parse URL
       */
      URI uri = new URI(url);
      // Throw away the 'asyncdb' protocol part of the URL
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

      return new MysqlConnectionManager(host, port, username, password, schema, properties);
    } catch (URISyntaxException e) {
      throw new DbException("Could not create connection to " + url, e);
    }
  }

  @Override
  public boolean canHandle(String protocol) {
    return PROTOCOL.equals(protocol);
  }

}
