package com.ly.train.flower.db.api;

import java.util.Map;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.support.ConnectionManagerFactory;


public class CheckConstructionMock implements ConnectionManagerFactory {
  private static ThreadLocal<CheckConstructionManager> lastInstance = new ThreadLocal<CheckConstructionManager>();

  @Override
  public ConnectionManager createConnectionManager(String url, String username, String password,
      Map<String, String> properties) throws DbException {
    CheckConstructionManager instance = new CheckConstructionManager(url, username, password, properties);
    lastInstance.set(instance);
    return instance;
  }



  public static CheckConstructionManager lastInstanceRequestedOnThisThread() {
    return lastInstance.get();
  }

  @Override
  public boolean canHandle(String protocol) {
    return "apimock".equals(protocol);
  }
}
