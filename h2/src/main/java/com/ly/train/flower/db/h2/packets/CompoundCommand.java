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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class CompoundCommand extends ClientToServerPacket {
  private final ClientToServerPacket[] commands;

  public CompoundCommand(ClientToServerPacket... commands) {
    super();
    this.commands = commands;
  }

  @Override
  public void writeToStream(DataOutputStream stream) throws IOException {
    for (ClientToServerPacket command : commands) {
      command.writeToStream(stream);
    }
  }

  @Override
  public int getLength() {
    int length = 0;
    for (ClientToServerPacket command : commands) {
      length += command.getLength();
    }
    return length;
  }

  @Override
  public String toString() {
    return "Commands{" + (commands == null ? null : Arrays.asList(commands)) + '}';
  }
}
