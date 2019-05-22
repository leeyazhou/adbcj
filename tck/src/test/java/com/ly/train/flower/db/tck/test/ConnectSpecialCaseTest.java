/**
 * Copyright © 2019 yazhou.li (lee_yazhou@163.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.db.tck.test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;

/**
 *
 */
public class ConnectSpecialCaseTest {
  private ConnectionManager connectionManager;

  @Parameters({"url", "user", "password"})
  @BeforeClass
  public void createConnectionManager(@Optional("asyncdb:mysql://10.100.216.147/asyncdb") String url,@Optional("root") String user,@Optional("UJ9FeAm3Yc@#E%IH8dLj6guyr5K&u#J3") String password) throws Exception {
    Configuration configuration = new Configuration();
    configuration.setUrl(url);
    configuration.setUsername(user);
    configuration.setPassword(password);
    this.connectionManager = ConnectionManagerProvider.createConnectionManager(configuration);
  }

  @Test(timeOut = 60000)
  public void testConnectBadCredentials() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);

    try {
      CompletableFuture<Connection> connectFuture = connectionManager.connect();

      connectFuture.handle((res, err) -> {
        Assert.assertNotNull(err);
        Assert.assertNull(res);
        latch.countDown();
        return null;
      });
      try {
        connectFuture.get();
        fail("Connect should have failed because of bad credentials");
      } catch (Exception e) {
        assertTrue(connectFuture.isDone(), "Connect future should be marked done even though it failed");
        assertTrue(!connectFuture.isCancelled(), "Connect future should not be marked as cancelled");
      }
      assertTrue(latch.await(1, TimeUnit.SECONDS), "Callback was not invoked in time");
    } finally {
      connectionManager.close();
    }
  }


}
