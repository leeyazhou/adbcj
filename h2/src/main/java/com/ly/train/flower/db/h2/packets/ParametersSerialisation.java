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
package com.ly.train.flower.db.h2.packets;

import com.ly.train.flower.db.api.support.SizeConstants;
import com.ly.train.flower.db.h2.DateTimeUtils;
import com.ly.train.flower.db.h2.decoding.H2Types;
import com.ly.train.flower.db.h2.decoding.IoUtils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;


final class ParametersSerialisation {


  public static void writeParams(DataOutputStream stream, Object[] params) throws IOException {
    stream.writeInt(params.length); // parameters
    for (Object param : params) {
      writeValue(stream, param);
    }
  }

  private static void writeValue(DataOutputStream stream, Object param) throws IOException {
    if (param == null) {
      stream.writeInt(H2Types.NULL.id());
    } else if (Integer.class.isInstance(param)) {
      stream.writeInt(H2Types.INTEGER.id());
      stream.writeInt(((Integer) param).intValue());
    } else if (Long.class.isInstance(param)) {
      stream.writeInt(H2Types.LONG.id());
      stream.writeLong(((Long) param).longValue());
    } else if (Double.class.isInstance(param)) {
      stream.writeInt(H2Types.DOUBLE.id());
      stream.writeDouble(((Double) param).doubleValue());
    } else if (Date.class.isInstance(param)) {
      stream.writeInt(H2Types.DATE.id());
      stream.writeLong(DateTimeUtils.dateValueFromDate(((Date) param).getTime()));
    } else if (String.class.isInstance(param)) {
      stream.writeInt(H2Types.STRING.id());
      IoUtils.writeString(stream, param.toString());
    } else if (BigDecimal.class.isInstance(param)) {
      stream.writeInt(H2Types.DECIMAL.id());
      IoUtils.writeString(stream, toString((BigDecimal) param));
    } else {
      throw new Error("TODO: Not implemented yet");
    }

  }

  public static int calculateParameterSize(Object[] params) {
    int size = SizeConstants.INT_SIZE; // parameter count
    for (Object param : params) {
      size += sizeOf(param);
    }
    return size;
  }

  private static int sizeOf(Object param) {
    if (param == null) {
      return SizeConstants.INT_SIZE;
    } else if (Integer.class.isInstance(param)) {
      return SizeConstants.INT_SIZE + SizeConstants.INT_SIZE;
    } else if (Long.class.isInstance(param)) {
      return SizeConstants.INT_SIZE + SizeConstants.LONG_SIZE;
    } else if (Double.class.isInstance(param)) {
      return SizeConstants.INT_SIZE + SizeConstants.DOUBLE_SIZE;
    } else if (Date.class.isInstance(param)) {
      return SizeConstants.INT_SIZE + SizeConstants.LONG_SIZE;
    } else if (BigDecimal.class.isInstance(param)) {
      return SizeConstants.INT_SIZE + SizeConstants.lengthOfString(toString((BigDecimal) param));
    } else if (String.class.isInstance(param)) {
      return SizeConstants.INT_SIZE + SizeConstants.INT_SIZE * SizeConstants.lengthOfString((String) param);
    } else {
      throw new Error("Serializing this type " + param.getClass() + " is not supported yet");
    }
  }


  private static String toString(BigDecimal decimal) {
    if (null == decimal) {
      return null;
    }
    String p = decimal.toPlainString();
    if (p.length() < 40) {
      return p;
    } else {
      return decimal.toString();
    }
  }
}
