package com.ly.train.flower.db.mysql.codec.model;

import com.ly.train.flower.db.mysql.codec.decoder.AbstractDecoder;
import com.ly.train.flower.db.mysql.codec.packets.response.AbstractResponse;

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
