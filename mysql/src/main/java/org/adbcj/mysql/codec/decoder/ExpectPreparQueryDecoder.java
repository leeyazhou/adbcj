package org.adbcj.mysql.codec.decoder;

import java.io.IOException;
import org.adbcj.DbCallback;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.MySqlPreparedStatement;
import org.adbcj.mysql.codec.BoundedInputStream;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.ErrorResponse;
import org.adbcj.mysql.codec.packets.response.OkResponse;
import org.adbcj.mysql.codec.packets.response.PreparedStatementToBuildResponse;
import org.adbcj.mysql.codec.packets.response.StatementPreparedEOFResponse;
import io.netty.channel.Channel;


public class ExpectPreparQueryDecoder extends AbstractDecoder {
  private final MySqlConnection connection;
  private final DbCallback<MySqlPreparedStatement> callback;
  private final StackTraceElement[] entry;

  public ExpectPreparQueryDecoder(MySqlConnection connection, DbCallback<MySqlPreparedStatement> callback,
      StackTraceElement[] entry) {
    this.callback = sandboxCallback(callback);
    this.entry = entry;
    this.connection = connection;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
      throws IOException {
    int fieldCount = in.read();
    if (fieldCount == AbstractResponseDecoder.RESPONSE_OK) {
      return handlePrepareQuery(length, packetNumber,
          OkResponse.interpretAsPreparedStatement(length, packetNumber, in));
    }
    if (fieldCount == AbstractResponseDecoder.RESPONSE_ERROR) {
      return handleError(decodeErrorResponse(in, length, packetNumber));
    } else {
      throw new IllegalStateException("Did not expect this response from the server");
    }
  }

  private ResponseWrapper handlePrepareQuery(int length, int packetNumber,
      OkResponse.PreparedStatementOK preparedStatement) {
    final PreparedStatementToBuildResponse statement =
        new PreparedStatementToBuildResponse(length, packetNumber, preparedStatement);
    final AbstractDecoder decoderState = FinishPrepareStatementDecoder.create(connection, statement, callback);
    if (decoderState instanceof AcceptNextResponseDecoder) {
      final StatementPreparedEOFResponse eof = new StatementPreparedEOFResponse(length, packetNumber, statement);
      callback.onComplete(new MySqlPreparedStatement(connection, eof), null);
      return new ResponseWrapper(decoderState, eof);
    } else {
      return new ResponseWrapper(decoderState, preparedStatement);
    }
  }

  private ResponseWrapper handleError(ErrorResponse errorResponse) {
    callback.onComplete(null, errorResponse.toException(entry));
    return new ResponseWrapper(new AcceptNextResponseDecoder(connection), errorResponse);
  }
}
