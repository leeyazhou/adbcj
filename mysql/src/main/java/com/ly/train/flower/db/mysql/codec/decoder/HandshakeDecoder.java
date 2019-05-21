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
package com.ly.train.flower.db.mysql.codec.decoder;

import static com.ly.train.flower.db.mysql.codec.util.IOUtil.safeSkip;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.CloseMode;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.model.ClientCapability;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.model.ServerStatus;
import com.ly.train.flower.db.mysql.codec.packets.response.ErrorResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.HandshakeResponse;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;
import io.netty.channel.Channel;

/**
 * 连接解码器
 * 
 * @author lee
 */
public class HandshakeDecoder extends AbstractDecoder {

  private final static Logger log = LoggerFactory.getLogger(HandshakeDecoder.class);

  /**
   * The salt size in a server greeting
   */
  public static final int SALT_SIZE = 8;

  /**
   * The size of the second salt in a server greeting
   */
  public static final int SALT2_SIZE = 12;

  /**
   * Number of unused bytes in server greeting
   */
  public static final int GREETING_UNUSED_SIZE = 13;

  private final DbCallback<Connection> callback;
  private final StackTraceElement[] entry;
  private final MySqlConnection connection;

  public HandshakeDecoder(DbCallback<Connection> callback, StackTraceElement[] entry, MySqlConnection connection) {
    this.callback = sandboxCallback(callback);
    this.entry = entry;
    this.connection = connection;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
      throws IOException {
    // try-to-parse error packet such 'Too many connections' when connecting.
    // @since 2017-09-01 little-pan
    final boolean initError;
    in.mark(Integer.MAX_VALUE);
    try {
      initError = (RESPONSE_ERROR == in.read());
    } finally {
      in.reset();
    }
    if (initError) {
      in.read(); // Skip error field count
      final ErrorResponse errorResponse = decodeErrorResponse(in, length, packetNumber);
      connection.close(CloseMode.CLOSE_FORCIBLY, (r, e) -> {
        if (e != null) {
          log.warn("Close connection abnormally", e);
        }
        callback.onComplete(null, errorResponse.toException(entry));
      });
      return resultWrapper(new AcceptNextResponseDecoder(connection), errorResponse);
    }
    // end error packet handler when connecting
    HandshakeResponse handshakeResponse = decodeHandshakeResponse(in, length, packetNumber);
    AbstractDecoder decoder = new ConnectedDecoder(callback, entry, connection);
    return resultWrapper(decoder, handshakeResponse);
  }

  private HandshakeResponse decodeHandshakeResponse(BoundedInputStream in, int length, int packetNumber)
      throws IOException {
    final int protocolVersion = IOUtil.safeRead(in);
    final String mysqlServerVersion = IOUtil.readNullTerminatedString(in, StandardCharsets.US_ASCII);
    final int threadId = IOUtil.readInt(in);

    byte[] salt = new byte[SALT_SIZE + SALT2_SIZE];
    in.readFully(salt, 0, SALT_SIZE);
    // Throw away 0 byte
    if (in.read() < 0) {
      throw new EOFException("Unexpected EOF. Expected to read 1 more byte");
    }

    Set<ClientCapability> serverCapabilities = IOUtil.readEnumSetShort(in, ClientCapability.class);
    MysqlCharacterSet charSet = MysqlCharacterSet.findById(in.read());
    Set<ServerStatus> serverStatus = IOUtil.readEnumSetShort(in, ServerStatus.class);
    safeSkip(in, GREETING_UNUSED_SIZE);

    in.readFully(salt, SALT_SIZE, SALT2_SIZE);// 挑战随机数
    // skip all plugin data for now
    in.readFully(new byte[in.getRemaining() - 1]);
    if (in.read() < 0) {
      throw new EOFException("Unexpected EOF. Expected to read 1 more byte");
    }

    return new HandshakeResponse(length, packetNumber, protocolVersion, mysqlServerVersion, threadId, salt,
        serverCapabilities, charSet, serverStatus);
  }

  @Override
  public String toString() {
    return "HandshakeDecoder";
  }
}
