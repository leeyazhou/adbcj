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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.ly.train.flower.db.jdbc.PlainJDBCConnection;

public abstract class InitDatabase {

  protected InitDatabase() {}

  public final void prepareMySQL(String url, String user, String password) throws Exception {
    beforeSetupScript(url, user, password);
    runSQLScript(url, user, password, setupScript());
  }

  protected void beforeSetupScript(String url, String user, String password) {

  }

  protected abstract String setupScript();

  public void cleanUp(String jdbcUrl, String user, String password) {
    runSQLScript(jdbcUrl, user, password, tearDownScript());
    afterCleanupScript();
  }

  protected void afterCleanupScript() {

  }

  protected abstract String tearDownScript();

  private void runSQLScript(String jdbcUrl, String user, String password, String script) {
    try {
      try (Connection connection = new PlainJDBCConnection(jdbcUrl, user, password, new HashMap<>()).getConnection()) {
        for (String line : setupSQL(script)) {
          Statement stmt = connection.createStatement();
          stmt.execute(line);
          stmt.close();

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private List<String> setupSQL(String resourceName) {
    InputStream sqlData = getClass().getClassLoader().getResourceAsStream(resourceName);
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(sqlData, "UTF8"));
      StringBuilder wholeFile = new StringBuilder();
      String line = reader.readLine();
      while (null != line) {
        wholeFile.append(line);
        line = reader.readLine();
      }
      return Arrays.asList(wholeFile.toString().split(";"));
    } catch (IOException e) {
      throw new RuntimeException("Couldn't read resource " + resourceName);
    } finally {
      try {
        sqlData.close();
      } catch (IOException e) {
        throw new RuntimeException("Couldn't read resource " + resourceName);
      }
    }
  }

}
