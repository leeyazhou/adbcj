package org.adbcj.mysql.netty;

import org.adbcj.mysql.codec.ClientRequest;
import org.adbcj.mysql.codec.MySqlClientEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class Encoder extends MessageToByteEncoder<ClientRequest> {
  private final static Logger log = LoggerFactory.getLogger(Encoder.class);

  private final MySqlClientEncoder encoder = new MySqlClientEncoder();

  @Override
  public void encode(ChannelHandlerContext ctx, ClientRequest msg, ByteBuf buffer) throws Exception {
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
