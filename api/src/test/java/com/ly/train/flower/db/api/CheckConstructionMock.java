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

import com.ly.train.flower.db.api.datasource.DataSource;
import com.ly.train.flower.db.api.datasource.DataSourceFactory;
import com.ly.train.flower.db.api.exception.DbException;


public class CheckConstructionMock implements DataSourceFactory {
  private static ThreadLocal<CheckConstructionManager> lastInstance = new ThreadLocal<CheckConstructionManager>();

  @Override
  public DataSource createDataSource(Configuration configuration) throws DbException {
    CheckConstructionManager instance = new CheckConstructionManager(configuration.getUrl(),
        configuration.getUsername(), configuration.getPassword(), configuration.getProperties());
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
