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

import java.util.EnumSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.support.LoginCredentials;
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
  private static final Set<ClientCapability> clientCapabilities =
      EnumSet.of(ClientCapability.LONG_PASSWORD, ClientCapability.FOUND_ROWS, ClientCapability.LONG_COLUMN_FLAG,
          ClientCapability.CONNECT_WITH_DB, ClientCapability.LOCAL_FILES, ClientCapability.PROTOCOL_4_1,
          ClientCapability.TRANSACTIONS, ClientCapability.SECURE_CONNECTION);

  private static final Set<ClientCapabilityExtend> clientCapabilityExtends =
      EnumSet.of(ClientCapabilityExtend.MULTI_RESULTS);

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
    AuthenticationRequest authenticationRequest = new AuthenticationRequest(loginCredentials, clientCapabilities,
        clientCapabilityExtends, MysqlCharacterSet.UTF8_UNICODE_CI, handshakeResponse.getSalt());
    handlerContext.channel().writeAndFlush(authenticationRequest);
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    this.handlerContext = ctx;
  }

}
