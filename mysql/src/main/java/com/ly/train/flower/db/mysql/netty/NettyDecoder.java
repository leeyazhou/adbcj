package com.ly.train.flower.db.mysql.netty;

import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.MySqlClientDecoder;
import com.ly.train.flower.db.mysql.codec.decoder.AbstractDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class NettyDecoder extends ByteToMessageDecoder {
  private final static Logger log = LoggerFactory.getLogger(NettyDecoder.class);
  private final MySqlClientDecoder decoder;
  private final MySqlConnection connection;

  public NettyDecoder(AbstractDecoder state, MySqlConnection connection) {
    this.decoder = new MySqlClientDecoder(state);
    this.connection = connection;
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
    // debug buffer since 2017-10-15 little-pan
    final boolean debug = log.isDebugEnabled();
    if (debug) {
      log.debug("Decoded buffer#{}: {}", buffer.hashCode(), buffer);
    }
    final InputStream in = new ByteBufInputStream(buffer);
    try {
      Object obj = decoder.decode(in, ctx.channel(), false);
      if (log.isDebugEnabled() && null != obj) {
        log.debug("Decoded message: {}", obj);
      }
      if (obj != null) {
        out.add(obj);
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