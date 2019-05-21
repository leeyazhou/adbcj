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
package com.ly.train.flower.db.h2.protocol;


public enum CommandCodes {
  SESSION_PREPARE(0), SESSION_CLOSE(1), COMMAND_EXECUTE_QUERY(2), COMMAND_EXECUTE_UPDATE(3), COMMAND_CLOSE(
      4), RESULT_CLOSE(
          7), SESSION_PREPARE_READ_PARAMS(11), SESSION_SET_ID(12), SESSION_SET_AUTOCOMMIT(15), SESSION_UNDO_LOG_POS(16);

  private final int statusValue;

  CommandCodes(int statusValue) {
    this.statusValue = statusValue;
  }

  public int getCommandValue() {
    return statusValue;
  }

  public boolean isCommand(int status) {
    return this.statusValue == status;
  }

  public static CommandCodes commandFor(int command) {
    for (CommandCodes cmd : values()) {
      if (cmd.getCommandValue() == command) {
        return cmd;
      }
    }
    throw new IllegalStateException("Cannot interpret command: " + command);
  }
}
