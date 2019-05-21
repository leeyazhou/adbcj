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
package com.ly.train.flower.db.mysql.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.mysql.codec.MySqlClientEncoder;
import com.ly.train.flower.db.mysql.codec.packets.request.AbstractRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<AbstractRequest> {
  private final static Logger log = LoggerFactory.getLogger(NettyEncoder.class);

  private final MySqlClientEncoder encoder = new MySqlClientEncoder();

  @Override
  public void encode(ChannelHandlerContext ctx, AbstractRequest msg, ByteBuf buffer) throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("Sending request: {}", msg);
    }
    ByteBufOutputStream out = new ByteBufOutputStream(buffer);
    try {
      encoder.encode(msg, out);
    } finally {
      out.close();
    }
  }
}
