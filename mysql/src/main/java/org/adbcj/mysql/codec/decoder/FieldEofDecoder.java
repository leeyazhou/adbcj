package org.adbcj.mysql.codec.decoder;

import java.io.IOException;
import java.util.List;
import org.adbcj.DbCallback;
import org.adbcj.DbException;
import org.adbcj.ResultHandler;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.BoundedInputStream;
import org.adbcj.mysql.codec.MysqlField;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.EofResponse;
import io.netty.channel.Channel;


class FieldEofDecoder<T> extends AbstractDecoder {
  private final List<MysqlField> fields;
  private final MySqlConnection connection;
  private final ResultHandler<T> eventHandler;
  private final T accumulator;
  private final DbCallback<T> callback;
  private final StackTraceElement[] entry;
  private RowDecoder.RowDecodingType decodingType;
  private DbException failure;

  public FieldEofDecoder(MySqlConnection connection, RowDecoder.RowDecodingType decodingType, List<MysqlField> fields,
      ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback, StackTraceElement[] entry,
      DbException failure) {
    this.decodingType = decodingType;
    this.fields = fields;
    this.callback = sandboxCallback(callback);
    this.connection = connection;
    this.eventHandler = eventHandler;
    this.accumulator = accumulator;
    this.entry = entry;
    this.failure = failure;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel) throws IOException {
    int fieldCount = in.read();

    try {
      eventHandler.endFields(accumulator);
      eventHandler.startResults(accumulator);
    } catch (Exception ex) {
      this.failure = DbException.attachSuppressedOrWrap(ex, entry, failure);
    }

    if (fieldCount != RESPONSE_EOF) {
      throw new IllegalStateException("Expected an EOF response from the server");
    }
    EofResponse fieldEof = decodeEofResponse(in, length, packetNumber, EofResponse.Type.FIELD);
    return result(new RowDecoder<T>(connection, decodingType, fields, eventHandler, accumulator, callback, entry, failure),
        fieldEof);
  }

  @Override
  public String toString() {
    return "FIELD-EOF";
  }


}
