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
import org.testng.annotations.Test;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.PreparedUpdate;
import com.ly.train.flower.db.api.Result;


public class InsertTest extends AbstractWithConnectionManagerTest {
  @Test
  public void returnsAutoIncrement() throws Exception {
    Connection connection = connectionManager.connect().get();
    Result result = connection.executeUpdate("INSERT INTO tableWithAutoId (textData) VALUES ('data')").get();
    Assert.assertEquals(result.getAffectedRows(), 1L);
    Assert.assertTrue(result.getGeneratedKeys().get(0).get(0).getLong() > 0);

    connection.close();
  }

  @Test
  public void returnsMutlitpleAutoIncrement() throws Exception {
    Connection connection = connectionManager.connect().get();
    Result result = connection
        .executeUpdate("INSERT INTO tableWithAutoId (textData) " + "VALUES ('data1'),('data2'),('data3');").get();
    Assert.assertEquals(result.getAffectedRows(), 3L);
    Assert.assertTrue(result.getGeneratedKeys().get(0).get(0).getLong() > 0);
    if (result.getGeneratedKeys().size() > 1) {
      // Some databases return just the last key
      Assert.assertTrue(result.getGeneratedKeys().get(1).get(0).getLong() > 0);
      Assert.assertTrue(result.getGeneratedKeys().get(2).get(0).getLong() > 0);
    }

    connection.close();
  }

  @Test
  public void returnsAutoIncrementPreparedQuery() throws Exception {
    Connection connection = connectionManager.connect().get();
    PreparedUpdate statement = connection.prepareUpdate("INSERT INTO tableWithAutoId (textData) " + "VALUES (?)").get();
    Result result = statement.execute("value prepared").get();

    Assert.assertEquals(result.getAffectedRows(), 1L);
    Assert.assertTrue(result.getGeneratedKeys().get(0).get(0).getLong() > 0);

    connection.close();
  }
}
