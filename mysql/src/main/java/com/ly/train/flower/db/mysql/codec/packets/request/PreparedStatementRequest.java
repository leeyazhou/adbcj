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
package com.ly.train.flower.db.mysql.codec.packets.request;

import com.ly.train.flower.db.api.support.SizeConstants;
import com.ly.train.flower.db.mysql.codec.model.MysqlType;
import com.ly.train.flower.db.mysql.codec.packets.Command;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class PreparedStatementRequest extends CommandRequest {
  private final int statementId;
  private final List<MysqlType> types;
  private final Object[] data;

  public PreparedStatementRequest(int statementId, List<MysqlType> types, Object[] data) {
    super(Command.STATEMENT_EXECUTE);
    this.statementId = statementId;
    this.types = types;
    this.data = data;
  }

  @Override
  public boolean hasPayload() {
    return true;
  }

  /**
   * Protocol see
   * http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#Execute_Packet_.28Tentative_Description.29
   */
  @Override
  protected void writePayLoad(OutputStream out) throws IOException {
    try {
      IOUtil.writeInt(out, statementId);
      out.write((byte) 0); // flags: 0: CURSOR_TYPE_NO_CURSOR
      IOUtil.writeInt(out, 1); // reserved for future use. Currently always 1.
      out.write(IOUtil.nullMask(data)); // null_bit_map
      out.write(1); // new_parameter_bound_flag
      for (MysqlType type : types) {
        IOUtil.writeShort(out, type.getId());
      }
      writeParameters(out);
    } catch (IOException e) {
      throw new RuntimeException("Unexpected IO Exception: " + e.getMessage(), e);
    }
  }

  @Override
  public int getLength() {
    return 1 + packetLength();
  }

  @Override
  public String toString() {
    return "PreparedStatementRequest{" + "statementId=" + statementId + '}';
  }

  int packetLength() {
    int size = SizeConstants.sizeOf(statementId) + SizeConstants.BYTE_SIZE // flags: 0: CURSOR_TYPE_NO_CURSOR
        + SizeConstants.sizeOf(1)// reserved for future use. Currently always 1.
        + IOUtil.nullMaskSize(data) + SizeConstants.BYTE_SIZE // new_parameter_bound_flag
        + SizeConstants.CHAR_SIZE * types.size();
    for (int i = 0; i < data.length; i++) {
      Object param = data[i];
      MysqlType type = types.get(i);
      if (null != param) {
        if (MysqlType.VARCHAR == type) {
          size += IOUtil.writeLengthCodedStringLength(param.toString(), StandardCharsets.UTF_8);
        }
        if (MysqlType.VAR_STRING == type) {
          size += IOUtil.writeLengthCodedStringLength(param.toString(), StandardCharsets.UTF_8);
        } else {
          throw new UnsupportedOperationException("Not yet implemented:" + type);
        }
      }
    }
    // Sanity test: Serialized output's length should be same as calculated size
    // {
    // ByteArrayOutputStream tt = new ByteArrayOutputStream();
    // try {
    // writePayLoad(tt);
    // } catch (IOException e) {
    // throw new RuntimeException(e.getMessage(),e);
    // }
    // if(tt.size()!=size){
    // throw new AssertionError("Size calculation is wrong. Should be "+tt.size() +"
    // but is " + size);
    // }
    // }
    return size;
  }

  private void writeParameters(OutputStream out) throws IOException {
    if (types.size() != data.length) {
      throw new IllegalStateException(
          "Expect type and data length to match. Type length: " + types.size() + " data length " + data.length);
    }
    for (int i = 0; i < data.length; i++) {
      Object param = data[i];
      MysqlType type = types.get(i);
      if (null != param) {
        if (MysqlType.VARCHAR == type) {
          IOUtil.writeLengthCodedString(out, param.toString(), StandardCharsets.UTF_8);
        }
        if (MysqlType.VAR_STRING == type) {
          IOUtil.writeLengthCodedString(out, param.toString(), StandardCharsets.UTF_8);
        } else {
          throw new UnsupportedOperationException("Not yet implemented:" + type);
        }
      }
    }
  }
}
