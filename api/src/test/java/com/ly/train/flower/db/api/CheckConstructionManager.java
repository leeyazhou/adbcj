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


import java.util.Map;
import org.junit.Assert;
import com.ly.train.flower.db.api.datasource.DataSource;
import com.ly.train.flower.db.api.exception.DbException;


public class CheckConstructionManager implements DataSource {
  private final String url;
  private final String username;
  private final String password;
  private final Map<String, String> properties;

  public CheckConstructionManager(String url, String username, String password, Map<String, String> properties) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.properties = properties;
  }

  public void assertURL(String url) {
    Assert.assertEquals(this.url, url);
  }

  public void assertUserName(String username) {
    Assert.assertEquals(this.username, username);
  }

  public void assertPassword(String password) {
    Assert.assertEquals(this.password, password);
  }

  public void assertProperty(String property, String value) {
    Assert.assertEquals(this.properties.get(property), value);
  }

  @Override
  public void connect(DbCallback<Connection> connected) {
    throw new Error("Mock does not support this operation");
  }

  @Override
  public void connect(String user, String password, DbCallback<Connection> connected) {
    throw new Error("Mock does not support this operation");
  }

  @Override
  public void close(DbCallback<Void> callback) {
    throw new Error("Mock does not support this operation");
  }

  @Override
  public void close(CloseMode mode, DbCallback<Void> callback) throws DbException {
    throw new Error("Mock does not support this operation");
  }


  @Override
  public boolean isClosed() {
    throw new Error("Mock does not support this operation");
  }
}
