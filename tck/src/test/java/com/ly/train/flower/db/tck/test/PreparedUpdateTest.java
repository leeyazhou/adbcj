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
import com.ly.train.flower.db.api.*;


public class PreparedUpdateTest extends AbstractWithConnectionManagerTest {
  @Test
  public void testCanInsert() throws Exception {
    Connection connection = connectionManager.connect().get();
    cleanUp(connection);
    PreparedUpdate insert = connection.prepareUpdate("INSERT INTO updates (id) VALUES (1)").get();
    insert.execute().get();


    PreparedUpdate update = connection.prepareUpdate("UPDATE updates SET id=? WHERE id=?").get();
    update.execute(42, 1);
    update.execute(4242, 42);


    PreparedQuery select = connection.prepareQuery("SELECT id FROM updates WHERE id=4242").get();

    final ResultSet rows = select.execute().get();
    Assert.assertEquals(1, rows.size());

    cleanUp(connection);
    connection.close();
  }

  private void cleanUp(Connection connection) throws Exception {
    connection.executeUpdate("DELETE FROM updates").get();
  }
}
