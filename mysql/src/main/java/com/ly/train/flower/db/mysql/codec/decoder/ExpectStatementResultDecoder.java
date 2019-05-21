package com.ly.train.flower.db.mysql.codec.decoder;

import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.ResultHandler;
import com.ly.train.flower.db.api.support.OneArgFunction;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.OKRegularResponse;


public class ExpectStatementResultDecoder<T> extends ExpectQueryResultDecoder<T> {

  public ExpectStatementResultDecoder(MySqlConnection connection, RowDecoder.RowDecodingType decodingType,
      ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback, StackTraceElement[] entry) {
    super(connection, decodingType, eventHandler, accumulator, callback, entry);
  }


  @Override
  protected ResponseWrapper handleOk(OKRegularResponse oKRegularResponse) {
    return ExpectUpdateResultDecoder.handleUpdateResult(connection, oKRegularResponse, callback, OneArgFunction.ID_FUNCTION);
  }
}
