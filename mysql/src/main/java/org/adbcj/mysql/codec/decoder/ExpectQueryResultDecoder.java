package org.adbcj.mysql.codec.decoder;

import java.io.IOException;
import java.util.ArrayList;
import org.adbcj.DbCallback;
import org.adbcj.DbException;
import org.adbcj.ResultHandler;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.codec.BoundedInputStream;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.ErrorResponse;
import org.adbcj.mysql.codec.packets.response.OkResponse;
import org.adbcj.mysql.codec.packets.response.ResultSetResponse;
import org.adbcj.mysql.codec.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExpectQueryResultDecoder<T> extends AbstractResponseStartDecoder {
  private static final Logger logger = LoggerFactory.getLogger(ExpectQueryResultDecoder.class);
  private final ResultHandler<T> eventHandler;
  private final T accumulator;
  protected final DbCallback<T> callback;
  private RowDecoder.RowDecodingType decodingType;
  private final StackTraceElement[] entry;
  private DbException failure;

  public ExpectQueryResultDecoder(MySqlConnection connection, RowDecoder.RowDecodingType decodingType, ResultHandler<T> eventHandler,
      T accumulator, DbCallback<T> callback, StackTraceElement[] entry) {
    super(connection);
    this.decodingType = decodingType;
    this.eventHandler = eventHandler;
    this.accumulator = accumulator;
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
    throw new Error("Not supported for query results");
  }

  @Override
  protected ResponseWrapper parseAsResult(int length, int packetNumber, BoundedInputStream in, int fieldCount)
      throws IOException {
    // Get the number of fields. The largest this can be is a 24-bit
    // integer so cast to int is ok
    int expectedFieldPackets = (int) IOUtil.readBinaryLengthEncoding(in, fieldCount);
    logger.trace("Field count {}", expectedFieldPackets);

    Long extra = null;
    if (in.getRemaining() > 0) {
      extra = IOUtil.readBinaryLengthEncoding(in);
    }
    try {
      eventHandler.startFields(accumulator);
    } catch (Exception any) {
      failure = DbException.wrap(any, entry);
    }
    return result(
        new FieldDecodingStateDecoder<T>(connection, decodingType, expectedFieldPackets, new ArrayList<>(), eventHandler,
            accumulator, callback, entry, failure),
        new ResultSetResponse(length, packetNumber, expectedFieldPackets, extra));
  }
}
