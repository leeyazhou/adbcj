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
package com.ly.train.flower.db.demo;

import java.util.ArrayList;
import org.h2.tools.Server;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.StandardProperties;
import com.ly.train.flower.db.api.datasource.DataSourceFactoryProvider;
import com.ly.train.flower.db.api.datasource.DataSource;

public class TutorialConnectionPool {


  private static final int TEST_CONNECTION_COUNT = 100;

  public static void main(String[] args) throws Exception {
    // First, let's start a demo H2 database server
    Server demoH2Db = DemoServer.startServer();
    Configuration configuration = new Configuration();
    configuration.setUrl("asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE");
    configuration.setUsername("asyncdb");
    configuration.setPassword("password1234");
    configuration.addProperty(StandardProperties.CONNECTION_POOL_ENABLE, "true");

    final DataSource dataSource = DataSourceFactoryProvider.createDataSource(configuration);

    long firstTime = System.currentTimeMillis();
    openCloseBunchOfConnections(dataSource);
    System.out.println(
        "First time: Time to connect: " + ((System.currentTimeMillis() - firstTime) / TEST_CONNECTION_COUNT) + "ms");


    long secondTime = System.currentTimeMillis();
    openCloseBunchOfConnections(dataSource);
    System.out.println("First time, pooled: Time to connect: "
        + ((System.currentTimeMillis() - secondTime) / TEST_CONNECTION_COUNT) + "ms");

    dataSource.close().get();

    demoH2Db.shutdown();
  }

  private static void openCloseBunchOfConnections(DataSource dataSource) throws Exception {
    ArrayList<Connection> connections = new ArrayList<>();
    for (int i = 0; i < TEST_CONNECTION_COUNT; i++) {
      connections.add(dataSource.connect().get());
    }
    for (Connection connection : connections) {
      connection.close().get();
    }
  }
}
