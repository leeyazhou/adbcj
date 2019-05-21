package org.adbcj.mysql.codec.model;

import org.adbcj.mysql.codec.decoder.AbstractDecoder;
import org.adbcj.mysql.codec.packets.response.AbstractResponse;

public class ResponseWrapper {
  private final AbstractResponse result;
  private final AbstractDecoder decoder;

  public ResponseWrapper(AbstractDecoder decoder, AbstractResponse result) {
    this.result = result;
    this.decoder = decoder;
  }

  public AbstractResponse getResult() {
    return result;
  }

  public AbstractDecoder getNewDecoder() {
    return decoder;
  }
}
