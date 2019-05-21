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
/**
 * 
 */
package com.ly.train.flower.db.mysql.netty;

import java.util.logging.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.mysql.codec.packets.request.AbstractRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelId;

/**
 * 
 * @author lee
 */
public class NettyChannel {
  private static final Logger logger = LoggerFactory.getLogger(NettyChannel.class);
  private final Channel channel;

  public NettyChannel(Channel channel) {
    this.channel = channel;
  }

  public void sendRequest(AbstractRequest request) {
    this.channel.writeAndFlush(request);
  }


  public void removeHandler(String handlerName) {
    channel.pipeline().remove(handlerName);
  }

  public void addLast(String name, ChannelHandler handler) {
    this.channel.pipeline().addLast(name, handler);
  }

  public ChannelFuture realClose(final StackTraceElement[] entry) {
    final Channel ch = channel;
    return ch.close();
  }

  public ChannelFuture close() {
    return channel.close();
  }

  public ChannelId channelId() {
    return channel.id();
  }

  public void autoRead() {
    channel.config().setAutoRead(true);
    channel.read();
  }
}
