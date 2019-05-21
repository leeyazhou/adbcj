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
import com.ly.train.flower.db.api.PreparedQuery;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.Row;
import java.math.BigDecimal;
import java.util.Date;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SupportedDataTypesTest extends AbstractWithConnectionManagerTest {


  @Test
  public void supportedInSelect() throws Exception {
    final Connection connection = connectionManager.connect().get();
    final ResultSet resultSet = connection.executeQuery("SELECT *, NULL FROM supporteddatatypes").get();
    final Row row = resultSet.get(0);

    assertValuesOfResult(row);

    connection.close().get();

  }

  @Test
  public void supportedInPreparedStatement() throws Exception {
    final Connection connection = connectionManager.connect().get();
    final PreparedQuery statement = connection.prepareQuery("SELECT *, NULL FROM supporteddatatypes").get();
    final ResultSet resultSet = statement.execute().get();
    final Row row = resultSet.get(0);

    assertValuesOfResult(row);


    connection.close().get();

  }

  @Test
  public void canBindDatatypesToParameters() throws Exception {
    final Connection connection = connectionManager.connect().get();
    final PreparedQuery statement = connection.prepareQuery("SELECT *, NULL FROM supporteddatatypes "
        + "WHERE intColumn=? " + "AND varCharColumn LIKE ? " + "AND bigIntColumn = ? " + "AND decimalColumn = ? "
        + "AND dateColumn < ? " + "AND doubleColumn < ? " + "AND textColumn LIKE ? ").get();
    final ResultSet resultSet = statement.execute(42, "4242", 42L, new BigDecimal("42.42"), new Date(), 42.4200001,
        "42-4242-42424242-4242424242424242-42424242-4242-42").get();

    final Row row = resultSet.get(0);
    Assert.assertNotNull(row);


    connection.close().get();

  }

  @Test
  public void canBindNullToParameter() throws Exception {
    final Connection connection = connectionManager.connect().get();
    final PreparedQuery statement = connection
        .prepareQuery("SELECT * FROM table_with_some_values " + "WHERE can_be_null_int=? OR can_be_null_varchar LIKE ?")
        .get();
    final ResultSet resultSet = statement.execute(42, null).get();
    final Row row = resultSet.get(0);
    Assert.assertNotNull(row);


    connection.close().get();
  }

  private void assertValuesOfResult(Row row) {
    assertEquals(row.get("intColumn").getInt(), 42);
    assertEquals(row.get("varCharColumn").getString(), "4242");
    assertEquals(row.get("bigIntColumn").getLong(), 42L);
    assertEquals(row.get("decimalColumn").getBigDecimal(), new BigDecimal("42.42"));
    assertEquals(row.get("dateColumn").getString(), "2012-05-03");
    assertTrue(row.get("dateColumn").getDate().getTime() < System.currentTimeMillis());
    assertTrue(row.get("dateTimeColumn").getString().startsWith("2012-05-16 16:57:51"));
    assertTrue(row.get("dateTimeColumn").getDate().getTime() < System.currentTimeMillis());
    assertEquals(row.get("timeColumn").getString(), "12:05:42");
    assertTrue(row.get("timeColumn").getDate().getTime() < System.currentTimeMillis());
    assertTrue(row.get("timeStampColumn").getString().startsWith("2012-05-16 17:10:36"));
    assertTrue(row.get("timeStampColumn").getDate().getTime() < System.currentTimeMillis());
    assertEquals(row.get("doubleColumn").getDouble(), 42.42, 0.0001);
    assertEquals(row.get("textColumn").getString(), "42-4242-42424242-4242424242424242-42424242-4242-42");
    assertEquals(row.get(10).getString(), null);
  }
}
