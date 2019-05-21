package com.ly.train.flower.db.mysql.codec.packets;

import com.ly.train.flower.db.mysql.codec.packets.response.AbstractResponse;

public class FailedToParseInputResponse extends AbstractResponse {
  private final Exception exception;

  public FailedToParseInputResponse(int packetLength, int packetNumber, Exception exception) {
    super(packetLength, packetNumber);
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
