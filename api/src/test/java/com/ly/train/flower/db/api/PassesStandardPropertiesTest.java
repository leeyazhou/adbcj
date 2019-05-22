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

import org.junit.Test;
import com.ly.train.flower.db.api.datasource.DataSourceFactoryProvider;
import com.ly.train.flower.db.api.datasource.DataSource;


public class PassesStandardPropertiesTest {

  @Test
  public void standardPropertiesArePassed() {

    Configuration configuration = new Configuration();
    configuration.setUrl("asyncdb:apimock:url");
    configuration.setUsername("sa");
    configuration.setPassword("pwd");
    final DataSource dataSource = DataSourceFactoryProvider.createDataSource(configuration);
    final CheckConstructionManager check = CheckConstructionMock.lastInstanceRequestedOnThisThread();

    check.assertURL("asyncdb:apimock:url");
    check.assertUserName("sa");
    check.assertPassword("pwd");
    check.assertProperty(StandardProperties.MAX_QUEUE_LENGTH, "256");
    dataSource.close();
  }

  @Test
  public void canOverrideProperty() {
    Configuration configuration = new Configuration();
    configuration.setUrl("asyncdb:apimock:url");
    configuration.setUsername("sa");
    configuration.setPassword("pwd");
    configuration.addProperty(StandardProperties.MAX_QUEUE_LENGTH, "128");
    final DataSource dataSource = DataSourceFactoryProvider.createDataSource(configuration);
    final CheckConstructionManager check = CheckConstructionMock.lastInstanceRequestedOnThisThread();

    check.assertProperty(StandardProperties.MAX_QUEUE_LENGTH, "128");
    dataSource.close();
  }

}
