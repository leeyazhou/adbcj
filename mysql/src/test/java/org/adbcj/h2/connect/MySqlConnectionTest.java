package org.adbcj.h2.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.adbcj.Connection;
import org.adbcj.ConnectionManager;
import org.adbcj.ConnectionManagerProvider;
import org.adbcj.DbCallback;
import org.adbcj.DbException;
import org.adbcj.Field;
import org.adbcj.ResultSet;
import org.adbcj.Row;
import org.adbcj.StandardProperties;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlConnectionTest {
  final static Logger log = LoggerFactory.getLogger(MySqlConnectionTest.class);

  @Test
  public void test() throws InterruptedException {
    final int n = 1;
    final String url = "adbcj:mysql://10.100.216.147/adbcjtck";
    final String username = "root", password = "UJ9FeAm3Yc@#E%IH8dLj6guyr5K&u#J3";
    final long tms = System.currentTimeMillis();
    final AtomicInteger success = new AtomicInteger(0);
    final CountDownLatch cnlat = new CountDownLatch(n);
    final CountDownLatch colat = new CountDownLatch(1);
    ConnectionManager connectionManager = null;
    try {
      final Map<String, String> props = new HashMap<>();
      props.put(StandardProperties.CONNECTION_POOL_ENABLE, "false");
      connectionManager = ConnectionManagerProvider.createConnectionManager(url, username, password, props);
      for (int i = 0; i < n; ++i) {
        log.debug("connecting-{} pending", i);
        CompletableFuture<Connection> cf = connectionManager.connect();
        cf.whenComplete((c, e) -> {
          cnlat.countDown();
          if (e != null) {
            log.warn("<<< connect error", e);
          }
          c.executeQuery("select * from user", new DbCallback<ResultSet>() {

            @Override
            public void onComplete(ResultSet result, DbException failure) {
              List<? extends Field> fs = result.getFields();
              System.err.println("结果属性：" + fs);
              for (Row row : result) {
                System.err.println("结果： " + row.get("id") + " : " + row.get("name"));
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
      Thread.sleep(5000);
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
