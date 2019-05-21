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
package com.ly.train.flower.db.api.support;

import java.util.*;
import com.ly.train.flower.db.api.Field;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.Row;
import com.ly.train.flower.db.api.Value;


public class DefaultRow extends AbstractMap<Object, Value> implements Row {

  private final ResultSet resultSet;
  private final Value[] values;

  private transient Set<java.util.Map.Entry<Object, Value>> entrySet;

  public DefaultRow(ResultSet resultSet, Value... values) {
    this.resultSet = resultSet;
    for (Value value : values) {
      if (value == null) {
        throw new AssertionError("Value cannot be null");
      }
    }
    this.values = values.clone();
  }

  @Override
  public Set<java.util.Map.Entry<Object, Value>> entrySet() {
    if (entrySet == null) {
      Set<java.util.Map.Entry<Object, Value>> set = new HashSet<Entry<Object, Value>>();
      final List<? extends Field> fields = resultSet.getFields();
      for (int i = 0; i < fields.size(); i++) {
        set.add(new AbstractMap.SimpleEntry<Object, Value>(fields.get(i), values[i]));

      }
      entrySet = Collections.unmodifiableSet(set);
    }
    return entrySet;
  }

  public ResultSet getResultSet() {
    return resultSet;
  }

  @Override
  public int size() {
    return values.length;
  }

  @Override
  public boolean containsKey(Object key) {
    return resultSet.getField(key) != null;
  }

  @Override
  public Value get(Object key) {
    Field field = resultSet.getField(key);
    if (field == null) {
      return null;
    }
    Value value = values[field.getIndex()];
    return value;
  }

  @Override
  public Value remove(Object key) {
    throw new UnsupportedOperationException("Results set rows are read-only");
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("Results set rows are read-only");
  }

  public Value[] getValues() {
    return values.clone();
  }
}
