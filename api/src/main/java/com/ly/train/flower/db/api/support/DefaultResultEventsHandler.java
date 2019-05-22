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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.Field;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.Value;
import com.ly.train.flower.db.api.handler.ResultHandler;

public final class DefaultResultEventsHandler<T extends ResultSet> implements ResultHandler<T> {
  private static final Logger logger = LoggerFactory.getLogger(DefaultResultEventsHandler.class);

  // TODO: Make this handler stateless
  private Value[] currentRow;
  private int rowIndex;

  public void startFields(T accumulator) {
    logger.debug("ResultSetEventHandler: startFields");
  }

  public void field(Field field, T accumulator) {
    logger.debug("ResultSetEventHandler: field");
    ((DefaultResultSet) accumulator).addField(field);
  }

  public void endFields(T accumulator) {
    logger.debug("ResultSetEventHandler: endFields");
  }

  public void startResults(T accumulator) {
    logger.debug("ResultSetEventHandler: startResults");
  }

  public void startRow(T accumulator) {
    logger.debug("ResultSetEventHandler: startRow");

    int columnCount = accumulator.getFields().size();
    currentRow = new Value[columnCount];
  }

  public void value(Value value, T accumulator) {
    logger.debug("ResultSetEventHandler: value");

    currentRow[rowIndex % currentRow.length] = value;
    rowIndex++;
  }

  public void endRow(T accumulator) {
    logger.debug("ResultSetEventHandler: endRow");
    DefaultRow row = new DefaultRow(accumulator, currentRow);
    ((DefaultResultSet) accumulator).addResult(row);
    currentRow = null;
  }

  public void endResults(T accumulator) {
    logger.debug("ResultSetEventHandler: endResults");
  }

  public void exception(Throwable t, T accumulator) {}
}
