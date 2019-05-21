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

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.ly.train.flower.db.api.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 *
 */
public class ConnectSpecialCaseTest {

  @Parameters({"url", "user", "password"})
  @Test(timeOut = 60000)
  public void testConnectBadCredentials(String url, String user, String password) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);

    ConnectionManager connectionManager =
        ConnectionManagerProvider.createConnectionManager(url, user, "__BADPASSWORD__");
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
