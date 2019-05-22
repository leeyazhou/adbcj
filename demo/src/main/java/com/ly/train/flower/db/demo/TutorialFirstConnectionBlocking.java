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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.h2.tools.Server;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.datasource.DataSourceFactoryProvider;
import com.ly.train.flower.db.api.datasource.DataSource;

public class TutorialFirstConnectionBlocking {

  public static void main(String[] args) {
    // First, let's start a demo H2 database server
    Server demoH2Db = DemoServer.startServer();
    Configuration configuration = new Configuration();
    configuration.setUrl("asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE");
    configuration.setUsername("asyncdb");
    configuration.setPassword("password1234");
    final DataSource dataSource = DataSourceFactoryProvider.createDataSource(configuration);

    // BLOCKING! Not recommended. Just example to get started
    CompletableFuture<Connection> connectionFuture = dataSource.connect();
    try {
      Connection connection = connectionFuture.get();
      System.out.println("Connected!");
      connection.close().get();
      System.out.println("Closed!");
      dataSource.close();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      e.printStackTrace();
      System.exit(-1);
    }


    demoH2Db.shutdown();
  }
}
