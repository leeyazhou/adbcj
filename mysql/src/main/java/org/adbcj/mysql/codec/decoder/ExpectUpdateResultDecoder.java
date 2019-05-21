package org.adbcj.mysql.codec.decoder;

import java.util.ArrayList;
import org.adbcj.DbCallback;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.MysqlResult;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.OkResponse;
import org.adbcj.support.OneArgFunction;


public class ExpectUpdateResultDecoder<T> extends ExpectOKDecoder<T> {

  private final OneArgFunction<MysqlResult, T> transformation;

  public ExpectUpdateResultDecoder(MySqlConnection connection, DbCallback<T> callback, StackTraceElement[] entry) {
    this(connection, callback, entry, OneArgFunction.ID_FUNCTION);
  }

  public ExpectUpdateResultDecoder(MySqlConnection connection, DbCallback<T> callback, StackTraceElement[] entry,
      OneArgFunction<MysqlResult, T> transformation) {
    super(connection, callback, entry);
    this.transformation = transformation;
  }

  @Override
  protected ResponseWrapper handleOk(OkResponse.RegularOK regularOK) {
    return handleUpdateResult(connection, regularOK, callback, transformation);
  }

  static <TFutureType> ResponseWrapper handleUpdateResult(MySqlConnection connection, OkResponse.RegularOK regularOK,
      DbCallback<TFutureType> futureToComplete, OneArgFunction<MysqlResult, TFutureType> transformation) {
    ArrayList<String> warnings = new ArrayList<String>(regularOK.getWarningCount());
    for (int i = 0; i < regularOK.getWarningCount(); i++) {
      warnings.add(regularOK.getMessage());
    }
    MysqlResult result = new MysqlResult(regularOK.getAffectedRows(), warnings, regularOK.getInsertId());
    futureToComplete.onComplete(transformation.apply(result), null);
    return new ResponseWrapper(new AcceptNextResponseDecoder(connection), regularOK);
  }
}
