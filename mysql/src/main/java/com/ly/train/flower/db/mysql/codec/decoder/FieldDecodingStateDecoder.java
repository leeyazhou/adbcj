package com.ly.train.flower.db.mysql.codec.decoder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.ResultHandler;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.MysqlField;
import com.ly.train.flower.db.mysql.codec.model.FieldFlag;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.model.MysqlType;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.ResultSetFieldResponse;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;
import io.netty.channel.Channel;


public class FieldDecodingStateDecoder<T> extends AbstractDecoder {
  private static final Logger logger = LoggerFactory.getLogger(FieldDecodingStateDecoder.class);
  private final int expectedAmountOfFields;
  private final List<MysqlField> fields;
  private final DbCallback<T> callback;
  private final StackTraceElement[] entry;
  private final MySqlConnection connection;
  private final ResultHandler<T> eventHandler;
  private final T accumulator;
  private RowDecoder.RowDecodingType decodingType;
  private DbException failure;

  public FieldDecodingStateDecoder(MySqlConnection connection, RowDecoder.RowDecodingType decodingType, int expectedAmountOfFields,
      List<MysqlField> fields, ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback,
      StackTraceElement[] entry, DbException failure) {
    this.decodingType = decodingType;
    this.expectedAmountOfFields = expectedAmountOfFields;
    this.fields = fields;
    this.callback = sandboxCallback(callback);
    this.entry = entry;
    this.connection = connection;
    this.eventHandler = eventHandler;
    this.accumulator = accumulator;
    this.failure = failure;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel) throws IOException {

    int fieldNo = fields.size();

    if (logger.isTraceEnabled()) {
      logger.trace("expectedAmountOfFields: {} current field {}", expectedAmountOfFields, fieldNo);

    }
    ResultSetFieldResponse resultSetFieldResponse = decodeFieldResponse(in, length, packetNumber, fieldNo);


    ArrayList<MysqlField> newFields = new ArrayList<MysqlField>(fieldNo + 1);
    newFields.addAll(fields);
    newFields.add(resultSetFieldResponse.getField());
    try {
      eventHandler.field(resultSetFieldResponse.getField(), accumulator);
    } catch (Exception any) {
      failure = DbException.attachSuppressedOrWrap(any, entry, failure);
    }

    if (expectedAmountOfFields > (fieldNo + 1)) {
      return resultWrapper(new FieldDecodingStateDecoder<T>(connection, decodingType, expectedAmountOfFields, newFields, eventHandler,
          accumulator, callback, entry, failure), resultSetFieldResponse);
    } else {
      return resultWrapper(
          new FieldEofDecoder<T>(connection, decodingType, newFields, eventHandler, accumulator, callback, entry, failure),
          resultSetFieldResponse);
    }
  }


  private ResultSetFieldResponse decodeFieldResponse(InputStream in, int packetLength, int packetNumber, int fieldNo)
      throws IOException {
    MysqlField field = parseField(in, fieldNo);
    return new ResultSetFieldResponse(packetLength, packetNumber, field);
  }

  public static MysqlField parseField(InputStream in, int fieldNo) throws IOException {
    String catalogName = IOUtil.readLengthCodedString(in, StandardCharsets.UTF_8);
    String schemaName = IOUtil.readLengthCodedString(in, StandardCharsets.UTF_8);
    String tableLabel = IOUtil.readLengthCodedString(in, StandardCharsets.UTF_8);
    String tableName = IOUtil.readLengthCodedString(in, StandardCharsets.UTF_8);
    String columnLabel = IOUtil.readLengthCodedString(in, StandardCharsets.UTF_8);
    String columnName = IOUtil.readLengthCodedString(in, StandardCharsets.UTF_8);
    // Skip filler
    if (in.read() < 0) {
      throw new EOFException("Unexpected EOF. Expected to read 1 more byte");
    }
    int characterSetNumber = IOUtil.readUnsignedShort(in);
    MysqlCharacterSet charSet = MysqlCharacterSet.findById(characterSetNumber);
    long length = IOUtil.readUnsignedInt(in);
    int fieldTypeId = in.read();
    MysqlType fieldType = MysqlType.findById(fieldTypeId);
    Set<FieldFlag> flags = IOUtil.readEnumSet(in, FieldFlag.class);
    int decimals = in.read();
    IOUtil.safeSkip(in, 2); // Skip filler
    long fieldDefault = IOUtil.readBinaryLengthEncoding(in);
    return new MysqlField(fieldNo, catalogName, schemaName, tableLabel, tableName, fieldType, columnLabel, columnName,
        0, // Figure out precision
        decimals, charSet, length, flags, fieldDefault);
  }

}
