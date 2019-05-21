package com.ly.train.flower.db.tck.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import com.ly.train.flower.db.api.CloseMode;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;
import com.ly.train.flower.db.api.StandardProperties;
import com.ly.train.flower.db.tck.InitDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public abstract class AbstractWithConnectionManagerTest {
  protected ConnectionManager connectionManager;
  private InitDatabase init;

  @Parameters({"jdbcUrl", "url", "user", "password", "setupClass", "connectionPool"})
  @BeforeClass
  public void createConnectionManager(String jdbcUrl, String url, String user, String password, String setupClass,
      boolean connectionPool) throws Exception {
    InitDatabase init = (InitDatabase) Class.forName(setupClass).newInstance();
    init.prepareMySQL(jdbcUrl, user, password);
    this.init = init;
    Map<String, String> props = properties();
    if (connectionPool) {
      props.put(StandardProperties.CONNECTION_POOL_ENABLE, "true");
    }
    this.connectionManager = ConnectionManagerProvider.createConnectionManager(url, user, password, props);
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
