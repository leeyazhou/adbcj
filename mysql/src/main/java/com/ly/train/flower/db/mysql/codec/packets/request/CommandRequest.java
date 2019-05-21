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

import java.io.IOException;
import java.io.OutputStream;
import com.ly.train.flower.db.mysql.codec.packets.Command;


public class CommandRequest extends AbstractRequest {

  private final Command command;


  public CommandRequest(Command command) {
    super();
    this.command = command;
  }

  public Command getCommand() {
    return command;
  }

  @Override
  public int getLength() {
    return 1;
  }

  @Override
  protected boolean hasPayload() {
    return false;
  }

  @Override
  public final void writeToOutputStream(OutputStream out) throws IOException {
    out.write(command.getCode());
    if (hasPayload()) {
      writePayLoad(out);
    }
  }

  protected void writePayLoad(OutputStream out) throws IOException {
  }

  @Override
  public String toString() {
    return "CommandRequest{" + "command=" + command + '}';
  }
}

