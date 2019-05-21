package org.adbcj.mysql.codec.decoder;

import static org.adbcj.mysql.codec.util.IOUtil.safeSkip;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.adbcj.CloseMode;
import org.adbcj.Connection;
import org.adbcj.DbCallback;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.BoundedInputStream;
import org.adbcj.mysql.codec.model.ClientCapability;
import org.adbcj.mysql.codec.model.MysqlCharacterSet;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.model.ServerStatus;
import org.adbcj.mysql.codec.packets.response.ErrorResponse;
import org.adbcj.mysql.codec.packets.response.HandshakeResponse;
import org.adbcj.mysql.codec.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.Channel;

/**
 * 连接解码器
 * 
 * @author lee
 */
public class ConnectingDecoder extends AbstractDecoder {

  private final static Logger log = LoggerFactory.getLogger(ConnectingDecoder.class);

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

  private final DbCallback<Connection> connectedCallback;
  private final StackTraceElement[] entry;
  private final MySqlConnection connection;

  public ConnectingDecoder(DbCallback<Connection> callback, StackTraceElement[] entry, MySqlConnection connection) {
    this.connectedCallback = sandboxCallback(callback);
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
        connectedCallback.onComplete(null, errorResponse.toException(entry));
      });
      return resultWrapper(new AcceptNextResponseDecoder(connection), errorResponse);
    }
    // end error packet handler when connecting
    HandshakeResponse handshakeResponse = decodeServerGreeting(in, length, packetNumber);
    return resultWrapper(new ConnectedDecoder(connectedCallback, entry, connection), handshakeResponse);
  }

  private HandshakeResponse decodeServerGreeting(BoundedInputStream in, int length, int packetNumber)
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
    return "CONNECTING";
  }
}
