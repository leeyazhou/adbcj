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
package com.ly.train.flower.db.mysql;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.CloseMode;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.support.AbstractConnectionManager;
import com.ly.train.flower.db.api.support.ConnectionPool;
import com.ly.train.flower.db.api.support.LoginCredentials;
import com.ly.train.flower.db.mysql.codec.decoder.AcceptNextResponseDecoder;
import com.ly.train.flower.db.mysql.codec.decoder.ConnectingDecoder;
import com.ly.train.flower.db.mysql.netty.NettyClientHandler;
import com.ly.train.flower.db.mysql.netty.NettyDecoder;
import com.ly.train.flower.db.mysql.netty.NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class MysqlConnectionManager extends AbstractConnectionManager {
  private static final Logger logger = LoggerFactory.getLogger(MysqlConnectionManager.class);
  private static final String ENCODER = MysqlConnectionManager.class.getName() + ".encoder";
  static final String DECODER = MysqlConnectionManager.class.getName() + ".decoder";
  private final LoginCredentials loginCredentials;

  private final Bootstrap bootstrap;
  private final AtomicInteger idCounter = new AtomicInteger();
  private final NioEventLoopGroup eventLoop;

  private final ConnectionPool<LoginCredentials, Channel> connectionPool;

  public MysqlConnectionManager(String host, int port, String username, String password, String schema,
      Map<String, String> properties) {
    super(properties);
    this.loginCredentials = new LoginCredentials(username, password, schema);
    this.eventLoop = new NioEventLoopGroup(0, new DefaultThreadFactory("db-io"));
    this.bootstrap = new Bootstrap().group(eventLoop).channel(NioSocketChannel.class);
    this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
    this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    this.bootstrap.option(ChannelOption.AUTO_READ, false);
    this.bootstrap.remoteAddress(new InetSocketAddress(host, port));
    this.bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {

      @Override
      public void initChannel(NioSocketChannel ch) throws Exception {
        ch.config().setAutoRead(false);
        ch.pipeline().addLast("loggingHandler", new LoggingHandler(LogLevel.DEBUG));
        ch.pipeline().addLast(ENCODER, new NettyEncoder());
        // ch.pipeline().addLast("handler", new NettyClientHandler());

      }
    });

    if (useConnectionPool) {
      this.connectionPool = new ConnectionPool<>();
    } else {
      this.connectionPool = null;
    }
  }

  public ConnectionPool<LoginCredentials, Channel> getConnectionPool() {
    return connectionPool;
  }

  @Override
  public void connect(DbCallback<Connection> connected) {
    this.connect(loginCredentials.getUserName(), loginCredentials.getPassword(), connected);
  }

  @Override
  public void connect(String user, String password, DbCallback<Connection> connected) {
    StackTraceElement[] entry = entryPointStack();
    if (isClosed()) {
      throw new DbException("Connection manager closed");
    }
    logger.debug("Starting connection");
    final MySqlHandler mySqlHandler = new MySqlHandler();
    final NettyClientHandler nettyClientHandler = new NettyClientHandler(loginCredentials, mySqlHandler);

    if (connectionPool != null) {
      Channel channel = connectionPool.tryAquire(loginCredentials);
      if (channel != null) {
        MySqlConnection connection =
            new MySqlConnection(loginCredentials, maxQueueLength(), this, channel, getStackTracingOption());
        channel.pipeline().addLast(DECODER, new NettyDecoder(new AcceptNextResponseDecoder(connection), connection));
        channel.pipeline().addLast("handler", nettyClientHandler);
        connected.onComplete(connection, null);
        return;
      }
    }


    final ChannelFuture channelFuture = bootstrap.connect();

    channelFuture.addListener((ChannelFutureListener) future -> {
      logger.debug("Physical connect completed");

      Channel channel = future.channel();

      if (!future.isSuccess()) {
        if (future.cause() != null) {
          channel.close();
          connected.onComplete(null, DbException.wrap(future.cause(), entry));
        }
        return;
      }

      MySqlConnection connection = new MySqlConnection(loginCredentials, maxQueueLength(), MysqlConnectionManager.this,
          channel, getStackTracingOption());
      addConnection(connection);
      channel.pipeline().addLast(DECODER,
          new NettyDecoder(new ConnectingDecoder(connected, entry, connection), connection));
      channel.pipeline().addLast("handler", nettyClientHandler);
      channel.config().setAutoRead(true);
      channel.read();
    });
  }


  @Override
  protected void doCloseConnection(Connection connection, CloseMode mode, DbCallback<Void> callback) {
    connection.close(mode, callback);
  }

  @Override
  protected void doClose(DbCallback<Void> callback, StackTraceElement[] entry) {
    new Thread("Closing MySQL ConnectionManager") {
      @Override
      public void run() {
        eventLoop.shutdownGracefully().addListener(future -> {
          DbException error = null;
          if (!future.isSuccess()) {
            error = DbException.wrap(future.cause(), entry);
          }
          callback.onComplete(null, error);
        });
      }
    }.start();

  }

  int nextId() {
    return idCounter.incrementAndGet();
  }


  void closedConnect(Connection connection) {
    removeConnection(connection);
  }
}


