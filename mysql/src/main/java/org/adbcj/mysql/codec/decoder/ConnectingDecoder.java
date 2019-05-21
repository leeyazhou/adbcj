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
import org.adbcj.mysql.codec.model.ClientCapabilities;
import org.adbcj.mysql.codec.model.MysqlCharacterSet;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.model.ServerStatus;
import org.adbcj.mysql.codec.packets.request.LoginRequest;
import org.adbcj.mysql.codec.packets.response.ErrorResponse;
import org.adbcj.mysql.codec.packets.response.ServerGreetingResponse;
import org.adbcj.mysql.codec.util.IOUtil;
import org.adbcj.support.LoginCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.Channel;


public class ConnectingDecoder extends AbstractDecoder {

  protected final static Logger log = LoggerFactory.getLogger(ConnectingDecoder.class);

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

  private final DbCallback<Connection> connected;
  private final StackTraceElement[] entry;
  private final MySqlConnection connection;
  private final LoginCredentials loginWith;

  public ConnectingDecoder(DbCallback<Connection> connected, StackTraceElement[] entry, MySqlConnection connection,
      LoginCredentials loginWith) {
    this.connected = sandboxCallback(connected);
    this.entry = entry;
    this.connection = connection;
    this.loginWith = loginWith;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel) throws IOException {
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
      final ErrorResponse error = decodeErrorResponse(in, length, packetNumber);
      connection.close(CloseMode.CLOSE_FORCIBLY, (r, e) -> {
        if (e != null) {
          log.warn("Close connection abnormally", e);
        }
        connected.onComplete(null, error.toException(entry));
      });
      return result(new AcceptNextResponseDecoder(connection), error);
    }
    // end error packet handler when connecting
    ServerGreetingResponse serverGreeting = decodeServerGreeting(in, length, packetNumber);
    LoginRequest loginRequest = new LoginRequest(loginWith, connection.getClientCapabilities(),
        connection.getExtendedClientCapabilities(), MysqlCharacterSet.UTF8_UNICODE_CI, serverGreeting.getSalt());
    channel.writeAndFlush(loginRequest);
    log.info("收到MySQL响应，现发送登录消息。Login : {}", loginWith);
    return result(new FinishLoginDecoder(connected, entry, connection), serverGreeting);
  }

  private ServerGreetingResponse decodeServerGreeting(BoundedInputStream in, int length, int packetNumber) throws IOException {
    int protocol = IOUtil.safeRead(in);
    String version = IOUtil.readNullTerminatedString(in, StandardCharsets.US_ASCII);
    int threadId = IOUtil.readInt(in);

    byte[] salt = new byte[SALT_SIZE + SALT2_SIZE];
    in.readFully(salt, 0, SALT_SIZE);
    // Throw away 0 byte
    if (in.read() < 0) {
      throw new EOFException("Unexpected EOF. Expected to read 1 more byte");
    }

    Set<ClientCapabilities> serverCapabilities = IOUtil.readEnumSetShort(in, ClientCapabilities.class);
    MysqlCharacterSet charSet = MysqlCharacterSet.findById(in.read());
    Set<ServerStatus> serverStatus = IOUtil.readEnumSetShort(in, ServerStatus.class);
    safeSkip(in, GREETING_UNUSED_SIZE);

    in.readFully(salt, SALT_SIZE, SALT2_SIZE);
    // skip all plugin data for now
    in.readFully(new byte[in.getRemaining() - 1]);
    if (in.read() < 0) {
      throw new EOFException("Unexpected EOF. Expected to read 1 more byte");
    }

    return new ServerGreetingResponse(length, packetNumber, protocol, version, threadId, salt, serverCapabilities, charSet,
        serverStatus);
  }

  @Override
  public String toString() {
    return "CONNECTING";
  }
}
