package com.ly.train.flower.db.mysql.codec.packets.request;

import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.packets.Command;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public class StringCommandRequest extends CommandRequest {
  private final String payload;
  private byte[] dataAsBytes = null;

  public StringCommandRequest(Command command, String payload) {
    super(command);
    this.payload = payload;
  }


  @Override
  public int getLength() {
    return 1 + payloadAsBinary().length;
  }

  @Override
  protected boolean hasPayload() {
    return payload != null;
  }

  @Override
  protected void writePayLoad(OutputStream out) throws IOException {
    out.write(payloadAsBinary());
  }

  @Override
  public String toString() {
    return "StringCommandRequest{" + "payload='" + payload + '\'' + '}';
  }

  private byte[] payloadAsBinary() {
    if (null != dataAsBytes) {
      return dataAsBytes;
    }
    try {
      dataAsBytes = payload.getBytes(MysqlCharacterSet.UTF8_UNICODE_CI.getCharsetName());
      return dataAsBytes;
    } catch (UnsupportedEncodingException e) {
      throw new DbException(e.getMessage(), e);
    }
  }


}
