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
package com.ly.train.flower.db.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ly.train.flower.db.api.Field;
import com.ly.train.flower.db.api.ResultHandler;
import com.ly.train.flower.db.api.Type;
import com.ly.train.flower.db.api.support.DefaultField;
import com.ly.train.flower.db.api.support.DefaultValue;


class ResultSetCopier {
  static <T> void fillResultSet(java.sql.ResultSet jdbcResultSet, ResultHandler<T> eventHandler, T accumulator)
      throws SQLException {
    ResultSetMetaData metaData = jdbcResultSet.getMetaData();
    int columnCount = metaData.getColumnCount();
    List<Field> fields = new ArrayList<Field>(columnCount);
    eventHandler.startFields(accumulator);

    for (int i = 1; i <= columnCount; i++) {
      Field field = new DefaultField(i - 1, metaData.getCatalogName(i), metaData.getSchemaName(i),
          metaData.getTableName(i), metaData.getTableName(i), Type.fromJdbcType(metaData.getColumnType(i)),
          metaData.getColumnLabel(i), metaData.getCatalogName(i), metaData.getPrecision(i), metaData.getScale(i),
          metaData.isAutoIncrement(i), metaData.isCaseSensitive(i), metaData.isCurrency(i), metaData.isNullable(i) == 1,
          metaData.isReadOnly(i), metaData.isSigned(i), metaData.getColumnClassName(i));
      fields.add(field);
      eventHandler.field(field, accumulator);
    }

    eventHandler.endFields(accumulator);

    eventHandler.startResults(accumulator);
    while (jdbcResultSet.next()) {
      eventHandler.startRow(accumulator);
      for (int i = 1; i <= columnCount; i++) {
        Field field = fields.get(i - 1);
        Object value;
        switch (field.getColumnType()) {
          case BIGINT:
            value = jdbcResultSet.getLong(i);
            break;
          case INTEGER:
            value = jdbcResultSet.getInt(i);
            break;
          case VARCHAR:
            value = jdbcResultSet.getString(i);
            break;
          case DECIMAL:
            value = jdbcResultSet.getString(i);
            break;
          case DATE:
            value = jdbcResultSet.getString(i);
            break;
          case TIME:
            value = jdbcResultSet.getString(i);
            break;
          case TIMESTAMP:
            value = jdbcResultSet.getString(i);
            break;
          case DOUBLE:
            value = jdbcResultSet.getDouble(i);
            break;
          case LONGVARCHAR:
            value = jdbcResultSet.getString(i);
            break;
          case CLOB:
            value = jdbcResultSet.getString(i);
            break;
          case NULL:
            value = null;
            break;
          default:
            throw new IllegalStateException("Don't know how to handle field to type " + field.getColumnType());
        }
        if (jdbcResultSet.wasNull()) {
          value = null;
        }
        eventHandler.value(new DefaultValue(value), accumulator);
      }
      eventHandler.endRow(accumulator);
    }
    eventHandler.endResults(accumulator);
  }

}
