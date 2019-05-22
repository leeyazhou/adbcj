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

import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.StandardProperties;


public class FailingOperationsTest extends AbstractWithConnectionManagerTest {

  @Test
  public void failingQuery() throws Exception {
    Connection connection = connectionManager.connect().get();
    try {
      connection.executeQuery("SELECT invalid query so it will throw").get();
      Assert.fail("Expect failure");
    } catch (ExecutionException ex) {
      Assert.assertTrue(ex.getCause() instanceof DbException);
      boolean foundThisTestInStack = false;
      for (StackTraceElement element : ex.getStackTrace()) {
        if (element.getMethodName().equals("failingQuery")) {
          foundThisTestInStack = true;
        }
      }
      Assert.assertTrue(foundThisTestInStack);

    }

  }

  @Override
  protected Map<String, String> properties() {
    final Map<String, String> config = super.properties();
    config.put(StandardProperties.CAPTURE_CALL_STACK, "true");
    return config;
  }
}
