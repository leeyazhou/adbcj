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

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.StandardProperties;
import com.ly.train.flower.db.api.datasource.DataSourceFactoryProvider;
import com.ly.train.flower.db.api.datasource.DataSource;

public class ConnectTest {
  final static Logger log = LoggerFactory.getLogger(ConnectTest.class);
  private Server server;

  @Test
  public void test() throws InterruptedException {
    final int n = 1;
    // h2 url schema - asyncdb:h2://host:port/db, no "tcp:" after "h2:"

    Configuration configuration = new Configuration();
    configuration.setUrl("asyncdb:h2://localhost:9092/test;DB_CLOSE_DELAY=-1");
    configuration.setUsername("sa");
    configuration.setPassword("");
    final long tms = System.currentTimeMillis();
    final AtomicInteger success = new AtomicInteger(0);
    final CountDownLatch cnlat = new CountDownLatch(n);
    final CountDownLatch colat = new CountDownLatch(1);
    DataSource cm = null;
    try {
      configuration.addProperty(StandardProperties.CONNECTION_POOL_ENABLE, "false");
      cm = DataSourceFactoryProvider.createDataSource(configuration);
      for (int i = 0; i < n; ++i) {
        log.debug("connecting-{} pending", i);
        CompletableFuture<Connection> cf = cm.connect();
        cf.whenComplete((c, e) -> {
          cnlat.countDown();
          if (e != null) {
            log.warn("<<< connect error", e);
          }
        }).thenCompose((c) -> {
          log.debug("connection is open? {}", c.isOpen());
          return c.close();
        }).thenRun(() -> {
          success.incrementAndGet();
          log.debug("<<< connection closed");
        });
        // cm.connect((c, e) -> {
        // cnlat.countDown();
        // if(e == null) {
        // throw new RuntimeException("error test");
        // //return;
        // }
        // log.warn("connct error: {}", e);
        // });
      }
      cnlat.await();
    } catch (final Throwable cause) {
      log.warn("fatal error", cause);
    } finally {
      if (cm != null) {
        cm.close().whenComplete((r, e) -> {
          if (e == null) {
            log.info("close completed");
          } else {
            log.warn("close error", e);
          }
          colat.countDown();
        });
      }
      colat.await();
      log.info("time: {}ms, ttl: {}, success: {}", (System.currentTimeMillis() - tms), n, success.get());
    }
  }

  @Before
  public void before() throws SQLException {
    server = Server.createTcpServer("-tcpAllowOthers", "-tcpDaemon", "-tcpPort", "9092", "-baseDir", "./h2testdb");
    // Server inspect = Server.createWebServer();
    // inspect.start();
    server.start();
  }

  @After
  public void after() {
    server.stop();
  }

}
