package org.adbcj.mysql.codec.decoder;

import java.io.IOException;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.BoundedInputStream;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.ErrorResponse;
import org.adbcj.mysql.codec.packets.response.OkResponse;
import io.netty.channel.Channel;

public abstract class AbstractResponseDecoder extends AbstractDecoder {

  protected final MySqlConnection connection;

  protected AbstractResponseDecoder(MySqlConnection connection) {
    this.connection = connection;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
      throws IOException {
    int fieldCount = in.read();
    if (fieldCount == RESPONSE_OK) {
      return handleOk(OkResponse.interpretAsRegularOk(length, packetNumber, in));
    }
    if (fieldCount == RESPONSE_ERROR) {
      return handleError(decodeErrorResponse(in, length, packetNumber));
    }
    if (fieldCount == RESPONSE_EOF) {
      throw new IllegalStateException("Did not expect an EOF response from the server");
    }
    return parseAsResult(length, packetNumber, in, fieldCount);
  }

  protected abstract ResponseWrapper handleError(ErrorResponse errorResponse);

  protected abstract ResponseWrapper handleOk(OkResponse.RegularOK regularOK);

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }

  protected ResponseWrapper parseAsResult(int length, int packetNumber, BoundedInputStream in, int fieldCount)
      throws IOException {
    throw new IllegalStateException(
        "This state: " + this + " does not expect a result which can be interpreted as " + "query result");
  }

}
