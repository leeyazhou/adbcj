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
