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
package com.ly.train.flower.db.api;

import org.junit.Assert;
import org.junit.Test;
import com.ly.train.flower.db.api.support.ConnectionPool;

public class ConnectionPoolTests {

  @Test
  public void startsEmpty() {
    ConnectionPool<String, String> toTest = new ConnectionPool<>();
    String noValue = toTest.tryAquire("test");

    Assert.assertNull(noValue);
  }

  @Test
  public void addAndReturnAConnetion() {
    ConnectionPool<String, String> toTest = new ConnectionPool<>();

    toTest.release("test", "test-conn");

    String conn = toTest.tryAquire("test");
    Assert.assertEquals("test-conn", conn);
  }


  @Test
  public void addAndReturnMultipleConnections() {
    ConnectionPool<String, String> toTest = new ConnectionPool<>();

    toTest.release("test", "test-conn-1");
    toTest.release("test", "test-conn-2");

    String conn1 = toTest.tryAquire("test");
    String conn2 = toTest.tryAquire("test");
    String noMoreConn = toTest.tryAquire("test");
    Assert.assertNull(noMoreConn);
    Assert.assertTrue(conn1.startsWith("test-conn"));
    Assert.assertTrue(conn2.startsWith("test-conn"));
    Assert.assertNotEquals(conn1, conn2);
  }

  @Test
  public void differentKeysHaveDifferentPools() {
    ConnectionPool<String, String> toTest = new ConnectionPool<>();

    toTest.release("test", "test-conn-1");
    toTest.release("other", "other-conn-2");

    String testConn = toTest.tryAquire("test");
    String otherConn = toTest.tryAquire("other");
    String noMoreConn = toTest.tryAquire("test");
    Assert.assertNull(noMoreConn);
    Assert.assertEquals("test-conn-1", testConn);
    Assert.assertEquals("other-conn-2", otherConn);
  }


}
