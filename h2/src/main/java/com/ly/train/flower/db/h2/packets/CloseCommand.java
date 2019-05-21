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
import com.ly.train.flower.db.api.support.SizeConstants;


public final class CloseCommand extends ClientToServerPacket {

  public CloseCommand() {
    super();
  }

  public static final int SESSION_CLOSE = 1;

  @Override
  public void writeToStream(DataOutputStream stream) throws IOException {
    stream.writeInt(SESSION_CLOSE);
  }

  @Override
  public int getLength() {
    return SizeConstants.INT_SIZE;
  }
}
