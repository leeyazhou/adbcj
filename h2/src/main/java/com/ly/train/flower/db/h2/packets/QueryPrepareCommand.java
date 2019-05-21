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
import com.ly.train.flower.db.h2.decoding.IoUtils;
import java.io.DataOutputStream;
import java.io.IOException;


public class QueryPrepareCommand extends ClientToServerPacket {
  private static final int SESSION_PREPARE = 0;
  private int id;
  private String sql;

  public QueryPrepareCommand(int id, String sql) {
    super();
    this.id = id;
    this.sql = sql;
  }

  @Override
  public void writeToStream(DataOutputStream stream) throws IOException {
    stream.writeInt(SESSION_PREPARE);
    stream.writeInt(id);
    IoUtils.writeString(stream, sql);
  }

  @Override
  public int getLength() {
    return SizeConstants.sizeOf(SESSION_PREPARE) + SizeConstants.sizeOf(id) + SizeConstants.sizeOf(sql) + 0;
  }

  @Override
  public String toString() {
    return "QueryPrepareCommand{" + "sql='" + sql + '\'' + '}';
  }
}
