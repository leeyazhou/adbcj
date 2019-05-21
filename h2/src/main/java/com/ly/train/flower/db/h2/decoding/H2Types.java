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
package com.ly.train.flower.db.h2.decoding;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import com.ly.train.flower.db.api.Type;

public enum H2Types {


  NULL(0, Type.OTHER, Object.class), INTEGER(4, Type.INTEGER, Integer.class), LONG(5, Type.BIGINT, Long.class), DECIMAL(
      6, Type.DECIMAL, BigDecimal.class), DOUBLE(7, Type.DOUBLE, Double.class), TIME(9, Type.TIME, Time.class), DATE(10,
          Type.DATE, Time.class), TIMESTAMP(11, Type.TIMESTAMP,
              Timestamp.class), STRING(13, Type.VARCHAR, String.class), CLOB(16, Type.CLOB, String.class);


  public static H2Types typeCodeToType(int typeCode) {
    for (H2Types type : values()) {
      if (typeCode == type.id) {
        return type;
      }
    }
    throw new IllegalArgumentException("Could not find type for " + typeCode);
  }



  private final int id;
  private final Type type;
  private final Class className;

  H2Types(int id, Type type, Class className) {
    this.id = id;
    this.type = type;
    this.className = className;
  }

  public Type getType() {
    return type;
  }

  public int id() {
    return id;
  }
}
