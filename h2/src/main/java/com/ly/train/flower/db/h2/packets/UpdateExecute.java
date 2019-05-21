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
import com.ly.train.flower.db.h2.protocol.CommandCodes;
import java.io.DataOutputStream;
import java.io.IOException;


public class UpdateExecute extends ClientToServerPacket {
  private final int id;
  private final Object[] params;
  private static final Object[] NO_PARAMS = new Object[0];

  public UpdateExecute(int id) {
    super();
    this.id = id;
    this.params = NO_PARAMS;
  }

  public UpdateExecute(int id, Object[] params) {
    super();
    this.id = id;
    this.params = params;
  }

  @Override
  public void writeToStream(DataOutputStream stream) throws IOException {
    stream.writeInt(CommandCodes.COMMAND_EXECUTE_UPDATE.getCommandValue());
    stream.writeInt(id);
    ParametersSerialisation.writeParams(stream, params);
  }

  @Override
  public int getLength() {
    return SizeConstants.INT_SIZE + // command execute update
        SizeConstants.INT_SIZE + // command id
        ParametersSerialisation.calculateParameterSize(params) + 0;
  }
}
