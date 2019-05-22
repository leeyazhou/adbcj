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
package com.ly.train.flower.db.tck.test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.Row;

/**
 * @author foooling@gmail.com
 */
public class ConnectionReusingTest extends AbstractWithConnectionManagerTest {
  private static Logger logger = LoggerFactory.getLogger(ConnectionReusingTest.class);
  private static final String alpha = "abcdefghijklmnopqrstuvwxyz";
  private static final int MAX_INT = 999999;
  private final Random random = new Random();

  private int randInt() {
    return randInt(MAX_INT);
  }

  private int randInt(int num) {
    return random.nextInt(num);
  }

  private char randChar() {
    return alpha.charAt(randInt() % 26);
  }

  private String randString(int length) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      stringBuilder.append(randChar());
    }
    return stringBuilder.toString();
  }


  private static final int CONN_NUM = 20;
  private final AtomicInteger finishCount = new AtomicInteger();
  private final AtomicInteger threadNum = new AtomicInteger();


  @Test(invocationCount = CONN_NUM, threadPoolSize = CONN_NUM, timeOut = 60000)
  public void reusedByNThreadsTest() throws Exception {

    final Connection connection = connectionManager.connect().get();
    CompletableFuture<Void> finalResult = new Object() {
      int randint = randInt();
      int num = threadNum.incrementAndGet();
      String tablename = randString(7) + "thread" + num;

      public CompletableFuture<Void> createTableIfNotExist() {
        logger.info(tablename + "-" + randint + " ---create table");
        return connection
            .executeUpdate("CREATE TABLE IF NOT EXISTS " + tablename + "(\n" + "  id int(11) NOT NULL AUTO_INCREMENT,\n"
                + "  name int(11) NOT NULL,\n" + "  PRIMARY KEY (id)\n" + ")")
            .thenCompose((res) -> continueAndInsert());
      }

      public CompletableFuture<Void> continueAndInsert() {
        logger.info(num + " : " + tablename + "-" + randint + " ---insert value");
        return connection.executeUpdate("INSERT into " + tablename + " (name) values (" + randint + ")")
            .thenCompose(res -> continueAndVerify());
      }

      public CompletableFuture<Void> continueAndVerify() {
        logger.info(num + " : " + tablename + "-" + randint + " ---select start ");
        connection.executeQuery("SELECT 1 from " + tablename + ";");
        return connection.executeQuery("SELECT * from " + tablename + ";").thenCompose(rs -> {
          Row r = rs.get(0);
          int resultint = r.get(1).getInt();
          logger.info(num + " : " + tablename + "-" + randint + " ---select got " + resultint);
          org.testng.Assert.assertEquals(resultint, randint);
          return finishWithDrop();
        });
      }

      public CompletableFuture<Void> finishWithDrop() {
        logger.info(num + " : " + tablename + "-" + randint + " ---dropping");
        return connection.executeUpdate("drop table " + tablename + ";").thenApply(r -> {
          finishCount.incrementAndGet();
          return null;

        });

      }
    }.createTableIfNotExist();

    Assert.assertEquals(null, finalResult.get());
    try {
      Thread.sleep(30000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test(dependsOnMethods = {"reusedByNThreadsTest"})
  public void isAllThreadsPassedTest() {
    logger.info(finishCount + "");
    Assert.assertEquals(finishCount.get(), CONN_NUM);

  }


}
