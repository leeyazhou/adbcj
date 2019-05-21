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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class QueueLengthTests extends AbstractWithConnectionManagerTest {

  @Test
  public void reportQueueOverflowAsCallbackException() throws Exception {
    Connection connection = connectionManager.connect().get();


    CompletableFuture<DbException> ex = new CompletableFuture<>();
    for (int i = 0; i < StandardProperties.DEFAULT_QUEUE_LENGTH * 2; i++) {
      connection.executeQuery("SELECT 1", (result, failure) -> {
        if (failure != null) {
          ex.complete(failure);
        }
      });
    }

    String msg = ex.get().getMessage();
    Assert.assertTrue(msg.contains(StandardProperties.MAX_QUEUE_LENGTH));
    Assert.assertTrue(msg.contains(String.valueOf(StandardProperties.MAX_QUEUE_LENGTH)));
  }

  @Parameters({"url", "user", "password",})
  @Test
  public void increaseQueueSize(String url, String user, String password) throws Exception {
    int limit = 512;
    Map<String, String> props = new HashMap<>();
    props.put(StandardProperties.MAX_QUEUE_LENGTH, String.valueOf(limit));
    ConnectionManager connectionManager = ConnectionManagerProvider.createConnectionManager(url, user, password, props);
    Connection connection = connectionManager.connect().get();

    try {
      CountDownLatch expectSuccess = new CountDownLatch(limit);
      AtomicReference<DbException> neverSet = new AtomicReference<>();
      for (int i = 0; i < limit; i++) {
        connection.executeQuery("SELECT 1", (result, failure) -> {
          if (failure != null) {
            neverSet.set(failure);
          } else {
            expectSuccess.countDown();
          }
        });
      }

      expectSuccess.await(30, TimeUnit.SECONDS);
      Assert.assertNull(neverSet.get());

    } finally {
      connectionManager.close().get();

    }


  }
}
