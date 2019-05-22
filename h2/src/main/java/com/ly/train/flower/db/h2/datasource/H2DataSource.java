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
package com.ly.train.flower.db.h2.datasource;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.*;
import com.ly.train.flower.db.api.datasource.AbstractDataSource;
import com.ly.train.flower.db.api.exception.DbConnectionClosedException;
import com.ly.train.flower.db.api.exception.DbException;
import com.ly.train.flower.db.api.support.ConnectionPool;
import com.ly.train.flower.db.api.support.LoginCredentials;
import com.ly.train.flower.db.h2.Encoder;
import com.ly.train.flower.db.h2.H2Connection;
import com.ly.train.flower.db.h2.Handler;
import com.ly.train.flower.db.h2.decoding.AnswerNextRequest;
import com.ly.train.flower.db.h2.decoding.Decoder;
import com.ly.train.flower.db.h2.packets.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.Map;


public class H2DataSource extends AbstractDataSource {
  private final static Logger logger = LoggerFactory.getLogger(H2DataSource.class);

  private final Bootstrap bootstrap;
  private static final String ENCODER = H2DataSource.class.getName() + ".encoder";
  public static final String DECODER = H2DataSource.class.getName() + ".decoder";
  private final String url;
  private final LoginCredentials defaultCredentials;
  private final Map<String, String> keys;
  private final NioEventLoopGroup eventLoop;
  private final ConnectionPool<LoginCredentials, Channel> connectionPool;

  public H2DataSource(Configuration configuration, Map<String, String> properties, Map<String, String> keys) {
    super(properties);
    this.url = configuration.getUrl();
    this.defaultCredentials =
        new LoginCredentials(configuration.getUsername(), configuration.getPassword(), configuration.getDatabase());
    this.keys = keys;

    this.eventLoop = new NioEventLoopGroup();
    this.bootstrap = new Bootstrap().group(eventLoop).channel(NioSocketChannel.class);
    this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
    this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    this.bootstrap.remoteAddress(new InetSocketAddress(configuration.getHost(), configuration.getPort()));
    this.bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {

      @Override
      public void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(ENCODER, new Encoder());
        ch.pipeline().addLast("handler", new Handler());
      }
    });

    if (useConnectionPool) {
      connectionPool = new ConnectionPool<>();
    } else {
      connectionPool = null;
    }
  }


  /**
   * @return the connectionPool
   */
  public ConnectionPool<LoginCredentials, Channel> getConnectionPool() {
    return connectionPool;
  }

  @Override
  public void connect(DbCallback<Connection> connected) {
    connect(defaultCredentials, connected);
  }

  @Override
  public final void connect(String user, String password, DbCallback<Connection> connected) {
    connect(new LoginCredentials(user, password, defaultCredentials.getDatabase()), connected);
  }

  private void connect(final LoginCredentials credentials, DbCallback<Connection> connected) {
    StackTraceElement[] entry = entryPointStack();
    if (isClosed()) {
      throw new DbConnectionClosedException("Connection manager closed");
    }
    logger.debug("Starting connection");

    if (connectionPool != null) {
      Channel channel = connectionPool.tryAquire(credentials);
      if (channel != null) {
        H2Connection dbConn = new H2Connection(credentials, maxQueueLength(), this, channel, getStackTracingOption());

        channel.pipeline().addFirst(DECODER, new Decoder(new AnswerNextRequest(dbConn, entry), dbConn, entry));

        connected.onComplete(dbConn, null);

        return;
      }
    }

    final ChannelFuture channelFuture = bootstrap.connect();


    channelFuture.addListener((ChannelFutureListener) future -> {
      logger.debug("Connect completed");

      Channel channel = future.channel();

      if (!future.isSuccess()) {
        channel.close();
        if (future.cause() != null) {
          connected.onComplete(null, DbException.wrap(future.cause(), entry));
        }
        return;
      }

      H2Connection connection =
          new H2Connection(credentials, maxQueueLength(), H2DataSource.this, channel, getStackTracingOption());
      channel.pipeline().addFirst(DECODER, new Decoder(connected, connection, entry));
      channel.writeAndFlush(new ClientHandshake(credentials.getDatabase(), url, credentials.getUserName(),
          credentials.getPassword(), keys));

      addConnection(connection);
    });
  }

  @Override
  protected void doCloseConnection(Connection connection, CloseMode mode, DbCallback<Void> callback) {
    connection.close(mode, callback);
  }

  @Override
  protected void doClose(DbCallback<Void> callback, StackTraceElement[] entry) {
    new Thread("Closing H2 ConnectionManager") {
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

  public void closedConnection(Connection connection) {
    removeConnection(connection);
  }

}
