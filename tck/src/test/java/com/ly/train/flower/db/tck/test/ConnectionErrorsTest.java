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
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;
import com.ly.train.flower.db.api.DbException;
import java.util.concurrent.ExecutionException;


public class ConnectionErrorsTest {
  @Parameters({"url"})
  @Test
  public void expectTimeoutWhenDatabaseNotAvailable(String url) throws Exception {
    assumeIsOnLocalhost(url);
    String unconnectableUrl = unconnectableURL(url);
    ConnectionManager connectionManager =
        ConnectionManagerProvider.createConnectionManager(unconnectableUrl, "root", "");
    try {
      Connection connection = connectionManager.connect().get();
      Assert.fail("should not be able to connect, but got" + connection);
    } catch (ExecutionException e) {
      // expected
      Assert.assertTrue(e.getCause() instanceof DbException);
    }
  }


  @Parameters({"url", "user", "password"})
  @Test
  public void expectErrorWithWrongSchema(String url, String user, String pwd) throws Exception {
    assumeIsOnLocalhost(url);
    String unconnectableUrl = wrongSchema(url);
    ConnectionManager connectionManager =
        ConnectionManagerProvider.createConnectionManager(unconnectableUrl, user, pwd);
    try {
      Connection connection = connectionManager.connect().get();
      Assert.fail("should not be able to connect, but got" + connection);
    } catch (ExecutionException e) {
      // expected
      Assert.assertTrue(e.getCause() instanceof DbException);
    }
  }

  private void assumeIsOnLocalhost(String url) {
    if (!url.contains("localhost")) {
      Assert.fail("This test assumes that the database is on localhost");
    }

  }

  private String unconnectableURL(String url) throws Exception {
    return url.replace("localhost", "not.reachable.localhost");
  }

  private String wrongSchema(String url) throws Exception {
    if (url.contains("h2")) {
      return url.replace("asyncdbtck", "invalidschema;IFEXISTS=TRUE");
    }
    return url.replace("asyncdbtck", "invalidschema");
  }

}

