package com.ly.train.flower.db.mysql.codec.decoder;

import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.ErrorResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.OKRegularResponse;


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
  protected ResponseWrapper handleOk(OKRegularResponse oKRegularResponse) {
    callback.onComplete(null, null);
    return new ResponseWrapper(new AcceptNextResponseDecoder(connection), oKRegularResponse);
  }
}
