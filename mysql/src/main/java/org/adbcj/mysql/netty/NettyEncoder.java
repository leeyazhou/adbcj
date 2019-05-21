package org.adbcj.mysql.netty;

import org.adbcj.mysql.codec.MySqlClientEncoder;
import org.adbcj.mysql.codec.packets.request.AbstractRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
