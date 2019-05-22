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
package com.ly.train.flower.db.mysql.codec.packets.response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.model.ServerStatus;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;

public abstract class OkResponse extends AbstractResponse {

  protected OkResponse(int packetLength, int packetNumber) {
    super(packetLength, packetNumber);
  }

  // util
  public static OKRegularResponse interpretAsRegularOk(int packetLength, int packetNumber, BoundedInputStream toParse)
      throws IOException {
    long affectedRows = IOUtil.readBinaryLengthEncoding(toParse);
    long insertId = IOUtil.readBinaryLengthEncoding(toParse);
    Set<ServerStatus> serverStatus = IOUtil.readEnumSetShort(toParse, ServerStatus.class);
    int warningCount = IOUtil.readUnsignedShort(toParse);
    String message = IOUtil.readFixedLengthString(toParse, toParse.getRemaining(), StandardCharsets.UTF_8);
    return new OKRegularResponse(packetLength, packetNumber, affectedRows, insertId, serverStatus, warningCount,
        message);
  }

  // util
  public static OKPreparedStatementResponse interpretAsPreparedStatement(int packetLength, int packetNumber,
      BoundedInputStream toParse) throws IOException {
    int handlerId = IOUtil.readInt(toParse);
    int columns = IOUtil.readShort(toParse);
    int params = IOUtil.readShort(toParse);
    @SuppressWarnings("unused")
    int filler = toParse.read();
    int warnings = IOUtil.readShort(toParse);
    return new OKPreparedStatementResponse(packetLength, packetNumber, handlerId, columns, params, warnings);
  }



}
