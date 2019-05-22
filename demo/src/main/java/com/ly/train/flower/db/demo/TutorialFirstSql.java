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
import org.h2.tools.Server;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.Result;
import com.ly.train.flower.db.api.Row;
import com.ly.train.flower.db.api.datasource.DataSourceFactoryProvider;
import com.ly.train.flower.db.api.datasource.DataSource;

public class TutorialFirstSql {

  public static void main(String[] args) throws Exception {
    // First, let's start a demo H2 database server
    Server demoH2Db = DemoServer.startServer();
    Configuration configuration = new Configuration();
    configuration.setUrl("asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE");
    configuration.setUsername("asyncdb");
    configuration.setPassword("password1234");
    final DataSource dataSource = DataSourceFactoryProvider.createDataSource(configuration);

    dataSource.connect().thenAccept(connection -> {
      CompletableFuture<Result> create =
          connection.executeUpdate("CREATE TABLE IF NOT EXISTS posts(\n" + "  id int NOT NULL AUTO_INCREMENT,\n"
              + "  title varchar(255) NOT NULL,\n" + "  content TEXT NOT NULL,\n" + "  PRIMARY KEY (id)\n" + ");");
      // NOTE: As long operation does not depend on the previous one, you can issue
      // then right away.
      // asyncdb will immediately send these requests to the database, when possible
      CompletableFuture<Result> firstInsert =
          connection.executeUpdate("INSERT INTO posts(title,content) VALUES('The Title','TheContent')");
      CompletableFuture<Result> secondInsert =
          connection.executeUpdate("INSERT INTO posts(title,content) VALUES('Another Title','More Content')");

      // Once you need to wait for previous requests, the Java 8 completable futures
      // will help you
      CompletableFuture<Void> createAndInsertDone = CompletableFuture.allOf(create, firstInsert, secondInsert);

      // And then, a simple query
      createAndInsertDone.thenCompose(res -> connection.executeQuery("SELECT * FROM posts")).thenAccept(queryResult -> {
        // NOTE: asyncdb default result sets are regular Java collections.
        // They start at index 0 (ZERO)
        // And interate like regular collections
        for (Row row : queryResult) {
          System.out.println("ID: " + row.get("ID").getLong() + " with title " + row.get("title").getString());
        }
      }).whenComplete((res, failure) -> {
        if (failure != null) {
          failure.printStackTrace();
        }
        connection.close();
        dataSource.close();
        demoH2Db.stop();
      });
    });

  }

}
