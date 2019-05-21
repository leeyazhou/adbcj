package org.adbcj.mysql.codec.decoder;

import java.io.IOException;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.BoundedInputStream;
import org.adbcj.mysql.codec.model.MySqlRequest;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.Channel;


public class AcceptNextResponseDecoder extends AbstractDecoder {
  private static final Logger logger = LoggerFactory.getLogger(AcceptNextResponseDecoder.class);
  private final MySqlConnection connection;

  public AcceptNextResponseDecoder(MySqlConnection connection) {
    this.connection = connection;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel) throws IOException {
    final MySqlRequest<?> request = connection.dequeRequest();
    if (logger.isDebugEnabled()) {
      logger.debug("Start parsing request: {}", request);
    }
    return request.getDecoder().decode(length, packetNumber, in, channel);
  }
}
