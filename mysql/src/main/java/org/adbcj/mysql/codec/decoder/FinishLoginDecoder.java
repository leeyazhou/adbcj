package org.adbcj.mysql.codec.decoder;

import org.adbcj.Connection;
import org.adbcj.DbCallback;
import org.adbcj.DbException;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.ErrorResponse;
import org.adbcj.mysql.codec.packets.response.OkResponse;

public class FinishLoginDecoder extends AbstractResponseDecoder {
  private final DbCallback<Connection> connected;
  private final StackTraceElement[] entry;

  public FinishLoginDecoder(DbCallback<Connection> connected, StackTraceElement[] entry, MySqlConnection connectionToBuild) {
    super(connectionToBuild);
    this.connected = sandboxCallback(connected);
    this.entry = entry;
  }

  @Override
  protected ResponseWrapper handleError(ErrorResponse errorResponse) {
    connected.onComplete(null, DbException.wrap(errorResponse.toException(entry), entry));
    return new ResponseWrapper(acceptNextResponse(), errorResponse);
  }

  @Override
  protected ResponseWrapper handleOk(OkResponse.RegularOK regularOK) {
    connected.onComplete(connection, null);
    return new ResponseWrapper(acceptNextResponse(), regularOK);
  }

  protected AcceptNextResponseDecoder acceptNextResponse() {
    return new AcceptNextResponseDecoder(connection);
  }
}
