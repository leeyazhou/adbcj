package org.adbcj.mysql.netty;

import org.adbcj.mysql.codec.packets.response.AbstractResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<AbstractResponse> {

  private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, AbstractResponse msg) throws Exception {
    logger.info("收到消息：{}", msg);
  }

}
