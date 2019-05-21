package com.ly.train.flower.db.mysql.netty;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.support.LoginCredentials;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.MySqlHandler;
import com.ly.train.flower.db.mysql.codec.model.ClientCapability;
import com.ly.train.flower.db.mysql.codec.model.ClientCapabilityExtend;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.packets.request.AuthenticationRequest;
import com.ly.train.flower.db.mysql.codec.packets.response.AbstractResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.HandshakeResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<AbstractResponse> {

  private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

  private final LoginCredentials loginCredentials;
  private final MySqlHandler mySqlHandler;
  private ChannelHandlerContext handlerContext;

  public NettyClientHandler(LoginCredentials loginCredentials, MySqlHandler mySqlHandler) {
    this.loginCredentials = loginCredentials;
    this.mySqlHandler = mySqlHandler;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, AbstractResponse response) throws Exception {
    logger.info("收到消息 {} ：{}", response, response.getClass().isAssignableFrom(HandshakeResponse.class));

    if (response.getClass().isAssignableFrom(HandshakeResponse.class)) {
      handleHandshake(response);
    } else {
      mySqlHandler.handleResponse(response);
    }

  }

  /**
   * 处理handshake
   * 
   * @param msg {@link HandshakeResponse}
   */
  private void handleHandshake(AbstractResponse msg) {
    HandshakeResponse handshakeResponse = (HandshakeResponse) msg;
    Set<ClientCapability> clientCapabilities = MySqlConnection.getClientCapabilities();
    Set<ClientCapabilityExtend> clientCapabilityExtend = MySqlConnection.getExtendedClientCapabilities();
    AuthenticationRequest authenticationRequest = new AuthenticationRequest(loginCredentials, clientCapabilities,
        clientCapabilityExtend, MysqlCharacterSet.UTF8_UNICODE_CI, handshakeResponse.getSalt());
    handlerContext.channel().writeAndFlush(authenticationRequest);
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    this.handlerContext = ctx;
  }

}
