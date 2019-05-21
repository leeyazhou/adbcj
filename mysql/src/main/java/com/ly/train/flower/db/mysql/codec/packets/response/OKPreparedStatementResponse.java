package com.ly.train.flower.db.mysql.codec.packets.response;

public class OKPreparedStatementResponse extends OkResponse {

  private final int handlerId;
  private final int columns;
  private final int params;
  private final int warnings;

  public OKPreparedStatementResponse(int packetLength, int packetNumber, int handlerId, int columns, int params,
      int warnings) {
    super(packetLength, packetNumber);
    this.handlerId = handlerId;
    this.columns = columns;
    this.params = params;
    this.warnings = warnings;
  }

  public int getHandlerId() {
    return handlerId;
  }

  public int getColumns() {
    return columns;
  }

  public int getParams() {
    return params;
  }

  public int getWarnings() {
    return warnings;
  }
}
