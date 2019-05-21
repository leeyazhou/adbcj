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
package com.ly.train.flower.db.mysql.codec.decoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.Field;
import com.ly.train.flower.db.api.ResultHandler;
import com.ly.train.flower.db.api.Value;
import com.ly.train.flower.db.api.support.DefaultValue;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.MysqlField;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.EofResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.ResultSetRowResponse;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;
import io.netty.channel.Channel;


public class RowDecoder<T> extends AbstractDecoder {
  private final RowDecodingType rowDecoding;
  private final List<MysqlField> fields;
  private final MySqlConnection connection;
  private final ResultHandler<T> eventHandler;
  private final T accumulator;
  private final DbCallback<T> callback;
  private final StackTraceElement[] entry;
  private DbException failure;

  public RowDecoder(MySqlConnection connection, RowDecodingType rowDecoding, List<MysqlField> fields,
      ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback, StackTraceElement[] entry,
      DbException failure) {
    this.rowDecoding = rowDecoding;
    this.fields = fields;
    this.connection = connection;
    this.eventHandler = eventHandler;
    this.accumulator = accumulator;
    this.callback = sandboxCallback(callback);
    this.entry = entry;
    this.failure = failure;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
      throws IOException {
    int fieldCount = in.read(); // This is only for checking for EOF
    if (fieldCount == RESPONSE_EOF) {
      try {
        eventHandler.endResults(accumulator);
      } catch (Exception ex) {
        failure = DbException.attachSuppressedOrWrap(ex, entry, failure);
      }
      if (this.failure == null) {
        callback.onComplete(accumulator, null);
      } else {
        callback.onComplete(null, failure);
      }
      EofResponse rowEof = decodeEofResponse(in, length, packetNumber, EofResponse.Type.ROW);
      return resultWrapper(new AcceptNextResponseDecoder(connection), rowEof);
    }

    Value[] values = rowDecoding.decode(in, fieldCount, this);
    try {
      eventHandler.startRow(accumulator);
      for (Value value : values) {
        eventHandler.value(value, accumulator);
      }
      eventHandler.endRow(accumulator);

    } catch (Exception ex) {
      failure = DbException.attachSuppressedOrWrap(ex, entry, failure);
    }
    return resultWrapper(
        new RowDecoder<T>(connection, rowDecoding, fields, eventHandler, accumulator, callback, entry, failure),
        new ResultSetRowResponse(length, packetNumber, values));

  }

  public enum RowDecodingType {
    BINARY {
      @Override
      public <T> Value[] decode(BoundedInputStream in, int fieldCount, RowDecoder<T> row) throws IOException {
        Value[] values = new Value[row.fields.size()];
        // 0 (packet header) should have been read by the calling method
        byte[] nullBits = new byte[(values.length + 7 + 2) / 8];
        in.readFully(nullBits);
        for (MysqlField field : row.fields) {
          Object value = null;
          if (hasValue(field.getIndex(), nullBits)) {
            switch (field.getMysqlType()) {
              case LONG:
                value = IOUtil.readInt(in);
                break;
              case LONGLONG:
                value = IOUtil.readLong(in);
                break;
              case VAR_STRING:
                value = IOUtil.readLengthCodedString(in, in.read(), StandardCharsets.UTF_8);
                break;
              case NEWDECIMAL:
                value = IOUtil.readLengthCodedString(in, in.read(), StandardCharsets.UTF_8);
                break;
              case DATE:
                value = IOUtil.readDate(in);
                break;
              case DATETIME:
                value = IOUtil.readDate(in);
                break;
              case TIME:
                value = IOUtil.readDate(in);
                break;
              case TIMESTAMP:
                value = IOUtil.readDate(in);
                break;
              case DOUBLE:
                value = Double.longBitsToDouble(IOUtil.readLong(in));
                break;
              case BLOB:
                value = IOUtil.readLengthCodedString(in, in.read(), StandardCharsets.UTF_8);
                break;
              case NULL:
                value = null;
                break;
              default:
                throw new IllegalStateException("Not yet implemented for type " + field.getMysqlType());
            }

          }

          values[field.getIndex()] = new DefaultValue(value);
        }

        return values;
      }


      private boolean hasValue(int valuePos, byte[] nullBitMap) {
        int bit = 4; // first two bits are reserved for future use
        int nullMaskPos = 0;
        boolean hasValue = false;
        for (int i = 0; i <= valuePos; i++) {
          hasValue = (nullBitMap[nullMaskPos] & bit) <= 0;
          if (((bit <<= 1) & 255) == 0) {
            bit = 1;
            nullMaskPos++;
          }
        }
        return hasValue;
      }
    },
    STRING_BASED {
      @Override
      public <T> Value[] decode(BoundedInputStream in, int fieldCount, RowDecoder<T> row) throws IOException {
        Value[] values = new Value[row.fields.size()];
        int i = 0;
        for (Field field : row.fields) {
          Object value = null;
          if (fieldCount != IOUtil.NULL_VALUE) {
            // We will have to move this as some datatypes will not be sent across the wire
            // as strings
            String strVal = IOUtil.readLengthCodedString(in, fieldCount, StandardCharsets.UTF_8);

            switch (field.getColumnType()) {
              case TINYINT:
                value = Byte.valueOf(strVal);
                break;
              case INTEGER:
                value = Integer.valueOf(strVal);
                break;
              case BIGINT:
                value = Long.valueOf(strVal);
                break;
              case VARCHAR:
                value = strVal;
                break;
              case DECIMAL:
                value = new BigDecimal(strVal);
                break;
              case DATE:
                value = strVal;
                break;
              case TIME:
                value = strVal;
                break;
              case TIMESTAMP:
                value = strVal;
                break;
              case DOUBLE:
                value = Double.parseDouble(strVal);
                break;
              case BLOB:
                value = strVal;
                break;
              default:
                throw new IllegalStateException("Don't know how to handle column type of " + field.getColumnType());
            }
          }
          values[field.getIndex()] = new DefaultValue(value);
          i++;
          if (i < row.fields.size()) {
            fieldCount = in.read();
          }
        }
        return values;
      }
    };


    public abstract <T> Value[] decode(BoundedInputStream in, int fieldCount, RowDecoder<T> row) throws IOException;
  }

}
