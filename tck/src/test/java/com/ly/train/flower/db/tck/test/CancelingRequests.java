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

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.ResultSet;


public class CancelingRequests extends AbstractWithConnectionManagerTest {


  @Test
  public void canCancelSelect() throws Exception {
    CountDownLatch newerCalled = new CountDownLatch(1);
    final Connection connection = dataSource.connect().get();

    final CompletableFuture<ResultSet> result = connection.executeQuery("SELECT SLEEP(2)");
    Thread.sleep(500);
    boolean cannotBeCanceled = result.cancel(true);
    result.handle((r, ex) -> {
      Assert.assertTrue(ex instanceof CancellationException);
      newerCalled.countDown();
      return r;
    });

    Assert.assertTrue(cannotBeCanceled);

    Assert.assertTrue(newerCalled.await(2, TimeUnit.SECONDS));

    connection.close();

  }

  @Test
  public void mayCanChancelNotYetRunningStatement() throws Exception {
    final Connection connection = dataSource.connect().get();


    final Future<ResultSet> runningStatment = connection.executeQuery("SELECT SLEEP(1)");
    final Future<ResultSet> toCancel = connection.executeQuery("SELECT SLEEP(2)");
    boolean canCancel = toCancel.cancel(true);

    if (canCancel) {
      try {
        toCancel.get();
        Assert.fail("Should throw CancellationException");
      } catch (CancellationException expected) {
        // expected
      }
    }

    connection.close();

  }
}
