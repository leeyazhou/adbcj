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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import com.ly.train.flower.db.api.CloseMode;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;
import com.ly.train.flower.db.api.StandardProperties;
import com.ly.train.flower.db.tck.InitDatabase;


public abstract class AbstractWithConnectionManagerTest {
  protected ConnectionManager connectionManager;
  private InitDatabase init;

  @Parameters({"jdbcUrl", "url", "user", "password", "setupClass", "connectionPool"})
  @BeforeClass
  public void createConnectionManager(String jdbcUrl, String url, String username, String password, String setupClass,
      boolean connectionPool) throws Exception {
    InitDatabase init = (InitDatabase) Class.forName(setupClass).newInstance();
    init.prepareMySQL(jdbcUrl, username, password);
    this.init = init;

    Configuration configuration = new Configuration();
    configuration.setUrl(url);
    configuration.setUsername(username);
    configuration.setPassword(password);
    if (connectionPool) {
      configuration.addProperty(StandardProperties.CONNECTION_POOL_ENABLE, "true");
    }


    this.connectionManager = ConnectionManagerProvider.createConnectionManager(configuration);
  }

  protected Map<String, String> properties() {
    return new HashMap<>();
  }

  @Parameters({"jdbcUrl", "user", "password",})
  @AfterClass
  public void closeConnectionManager(String jdbcUrl, String user, String password) throws Exception {
    CompletableFuture<Void> closeFuture = connectionManager.close(CloseMode.CANCEL_PENDING_OPERATIONS);
    closeFuture.get();
    init.cleanUp(jdbcUrl, user, password);
  }
}
