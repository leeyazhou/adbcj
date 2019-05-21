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
package com.ly.train.flower.db.h2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.h2.packets.ClientToServerPacket;
import java.io.DataOutputStream;


class Encoder extends MessageToByteEncoder<ClientToServerPacket> {
  private final static Logger logger = LoggerFactory.getLogger(Encoder.class);


  @Override
  public void encode(ChannelHandlerContext ctx, ClientToServerPacket request, ByteBuf buffer) throws Exception {
    final int wi = buffer.writerIndex();
    ByteBufOutputStream out = new ByteBufOutputStream(buffer);
    DataOutputStream dataOutputStream = new DataOutputStream(out);
    request.writeToStream(dataOutputStream);
    dataOutputStream.close();
    out.close();
    // debug packet sent since 2017-09-19 pzp
    if (logger.isDebugEnabled()) {
      final int length = buffer.writerIndex() - wi;
      logger.debug("Packet sent: request - {}\n{}", request, ByteBufUtil.prettyHexDump(buffer, wi, length));
    }
  }
}
