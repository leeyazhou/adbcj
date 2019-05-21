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
package com.ly.train.flower.db.h2.decoding;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.h2.H2Connection;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.List;


public class Decoder extends ByteToMessageDecoder {
  private static final Logger logger = LoggerFactory.getLogger(Decoder.class);

  private static Object DecodedToken = new Object();
  private DecoderState currentState;
  private H2Connection connection;

  public Decoder(DbCallback<Connection> initialStateCompletion, H2Connection connection, StackTraceElement[] entry) {
    currentState = new FirstServerHandshake(initialStateCompletion, connection, entry);
    this.connection = connection;
  }

  public Decoder(DecoderState currentState, H2Connection connection, StackTraceElement[] entry) {
    this.currentState = currentState;
    this.connection = connection;
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
    // debug received packet since 2017-09-19 pzp
    if (logger.isDebugEnabled()) {
      final int ri = buffer.readerIndex(), length = buffer.readableBytes();
      logger.debug("Packet recv: \n{}", ByteBufUtil.prettyHexDump(buffer, ri, length));
    }
    InputStream in = new ByteBufInputStream(buffer);
    in.mark(Integer.MAX_VALUE);
    try {
      final ResultAndState resultState = currentState.decode(new DataInputStream(in), ctx.channel());
      currentState = resultState.getNewState();
      if (resultState.isWaitingForMoreInput()) {
        in.reset();
      } else {
        out.add(DecodedToken);
      }
    } finally {
      in.close();
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    connection.tryCompleteClose(null);
  }
}
