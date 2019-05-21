package org.adbcj.mysql.codec.decoder;

import org.adbcj.DbCallback;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.ErrorResponse;
import org.adbcj.mysql.codec.packets.response.OkResponse;


public class ExpectOKDecoder<T> extends AbstractResponseDecoder {

  protected final DbCallback<T> callback;
  private final StackTraceElement[] entry;

  public ExpectOKDecoder(MySqlConnection connection, DbCallback<T> callback, StackTraceElement[] entry) {
    super(connection);
    this.callback = sandboxCallback(callback);
    this.entry = entry;
  }

  @Override
  protected ResponseWrapper handleError(ErrorResponse errorResponse) {
    callback.onComplete(null, errorResponse.toException(entry));
    return new ResponseWrapper(new AcceptNextResponseDecoder(connection), errorResponse);
  }

  @Override
  protected ResponseWrapper handleOk(OkResponse.RegularOK regularOK) {
    callback.onComplete(null, null);
    return new ResponseWrapper(new AcceptNextResponseDecoder(connection), regularOK);
  }
}
