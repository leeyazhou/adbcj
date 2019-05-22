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

import java.util.Random;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;

/**
 *
 */
public class PopulateLarge {

  public static void main(String[] args) throws Exception {
    Configuration configuration = new Configuration();
    configuration.setUrl("asyncdb:mysqlnetty://localhost/asyncdb");
    configuration.setUsername("asyncdb");
    configuration.setPassword("asyncdb");
    Configuration configuration2 = new Configuration();
    configuration2.setUrl("asyncdb:postgresql-netty://localhost/asyncdb");
    configuration2.setUsername("asyncdb");
    configuration2.setPassword("asyncdb");

    ConnectionManager mysqlCM = ConnectionManagerProvider.createConnectionManager(configuration);
    ConnectionManager pgCM = ConnectionManagerProvider.createConnectionManager(configuration2);

    Connection mysql = mysqlCM.connect().get();
    Connection pg = pgCM.connect().get();

    final String insertTemplate = "INSERT INTO large (a, b, c) VALUES ('%s', '%s', '%s')";
    for (int i = 0; i < 998; i++) {
      String a = randString();
      String b = randString();
      String c = randString();
      final String insert = String.format(insertTemplate, a, b, c);
      mysql.executeUpdate(insert).get();
      pg.executeUpdate(insert).get();
    }
    // mysql.close(false).get();
    // pg.close(false).get();
    // mysqlCM.close(true);
    // pgCM.close(true);
  }

  private static String randString() {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 255; i++) {
      char c = (char) (rand.nextInt(26) + 65);
      sb.append(c);
    }
    return sb.toString();
  }

}
