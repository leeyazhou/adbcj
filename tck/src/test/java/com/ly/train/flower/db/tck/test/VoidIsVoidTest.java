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
import org.testng.annotations.Test;
import com.ly.train.flower.db.api.Connection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class VoidIsVoidTest extends AbstractWithConnectionManagerTest {

  @Test
  public void testCommitTransaction() throws Exception {
    Connection connection = connectionManager.connect().get();
    try {
      connection.beginTransaction();
      assertFutureIsVoid(connection.commit());
    } finally {
      connection.close();
    }
  }

  @Test
  public void testRollbackTransaction() throws Exception {
    Connection connection = connectionManager.connect().get();
    try {
      connection.beginTransaction();
      assertFutureIsVoid(connection.rollback());
    } finally {
      connection.close();
    }
  }

  @Test
  public void testClose() throws Exception {
    Connection connection = connectionManager.connect().get();
    try {
      final CompletableFuture<Void> future = connection.close();
      assertFutureIsVoid(future);
    } finally {
      connection.close();
    }
  }

  private void assertFutureIsVoid(CompletableFuture<Void> future) throws Exception {
    final Object object = future.get();
    Assert.assertTrue(object == null);
  }
}
