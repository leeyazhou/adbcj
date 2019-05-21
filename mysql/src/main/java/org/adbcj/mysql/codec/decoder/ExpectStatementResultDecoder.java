package org.adbcj.mysql.codec.decoder;

import org.adbcj.DbCallback;
import org.adbcj.ResultHandler;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.OkResponse;
import org.adbcj.support.OneArgFunction;


public class ExpectStatementResultDecoder<T> extends ExpectQueryResultDecoder<T> {

  public ExpectStatementResultDecoder(MySqlConnection connection, RowDecoder.RowDecodingType decodingType,
      ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback, StackTraceElement[] entry) {
    super(connection, decodingType, eventHandler, accumulator, callback, entry);
  }


  @Override
  protected ResponseWrapper handleOk(OkResponse.RegularOK regularOK) {
    return ExpectUpdateResultDecoder.handleUpdateResult(connection, regularOK, callback, OneArgFunction.ID_FUNCTION);
  }
}
