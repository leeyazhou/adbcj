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
package com.ly.train.flower.db.h2.connect;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.Field;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.Row;
import com.ly.train.flower.db.api.StandardProperties;

public class MySqlConnectionTest {
  final static Logger log = LoggerFactory.getLogger(MySqlConnectionTest.class);

  @Test
  public void testMySQL() throws InterruptedException {
    final int n = 1;
    Configuration configuration = new Configuration();
    configuration.setUrl("asyncdb:mysql://10.100.216.147/asyncdb");
    configuration.setUsername("root");
    configuration.setPassword("UJ9FeAm3Yc@#E%IH8dLj6guyr5K&u#J3");

    final long tms = System.currentTimeMillis();
    final AtomicInteger success = new AtomicInteger(0);
    final CountDownLatch cnlat = new CountDownLatch(n);
    final CountDownLatch colat = new CountDownLatch(1);
    ConnectionManager connectionManager = null;
    try {
      configuration.addProperty(StandardProperties.CONNECTION_POOL_ENABLE, "false");
      connectionManager = ConnectionManagerProvider.createConnectionManager(configuration);
      for (int i = 0; i < n; ++i) {
        log.debug("connecting-{} pending", i);
        CompletableFuture<Connection> cf = connectionManager.connect();
        cf.whenComplete((c, e) -> {
          cnlat.countDown();
          if (e != null) {
            log.warn("<<< connect error", e);
          }
          c.executeQuery("select * from user where id = 10", new DbCallback<ResultSet>() {

            @Override
            public void onComplete(ResultSet result, DbException failure) {
              List<? extends Field> fs = result.getFields();
              System.err.println("结果属性：" + fs + "\n");
              for (Row row : result) {
                log.info("结果： " + row.get("id") + " : " + row.get("name"));
              }
              if (failure != null) {
                failure.printStackTrace();
              }
            }
          });
          c.executeQuery("select version()", new DbCallback<ResultSet>() {

            @Override
            public void onComplete(ResultSet result, DbException failure) {
              List<? extends Field> fs = result.getFields();
              System.err.println("结果属性：" + fs + "\n");
              for (Row row : result) {
                log.info("结果： " + row.get("version()") + " : " + row.get("name"));
              }
              if (failure != null) {
                failure.printStackTrace();
              }
            }
          });
        }).thenCompose((c) -> {
          log.debug("connection is open? {}", c.isOpen());
          return c.close();
        }).thenRun(() -> {
          success.incrementAndGet();
          log.debug("<<< connection closed");
        });
      }
      cnlat.await();
    } catch (final Throwable cause) {
      log.warn("fatal error", cause);
    } finally {
      Thread.sleep(15000);
      if (connectionManager != null) {
        connectionManager.close().whenComplete((r, e) -> {
          if (e == null) {
            log.info("close completed");
          } else {
            log.warn("close error", e);
          }
          colat.countDown();
        });
      }
      colat.await();
      log.info("耗时time: {}ms, ttl: {}, success: {}", (System.currentTimeMillis() - tms), n, success.get());
    }
  }

}
