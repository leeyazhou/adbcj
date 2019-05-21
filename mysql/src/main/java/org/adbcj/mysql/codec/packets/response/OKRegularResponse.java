package org.adbcj.mysql.codec.packets.response;

import java.util.Collections;
import java.util.Set;
import org.adbcj.mysql.codec.model.ServerStatus;

public class OKRegularResponse extends OkResponse {

  private final long affectedRows;
  private final long insertId;
  private final Set<ServerStatus> serverStatus;
  private final int warningCount;
  private final String message;

  public OKRegularResponse(int length, int packetNumber, long affectedRows, long insertId,
      Set<ServerStatus> serverStatus, int warningCount, String message) {
    super(length, packetNumber);
    this.affectedRows = affectedRows;
    this.insertId = insertId;
    this.serverStatus = Collections.unmodifiableSet(serverStatus);
    this.warningCount = warningCount;
    this.message = message;
  }

  public long getAffectedRows() {
    return affectedRows;
  }

  public long getInsertId() {
    return insertId;
  }

  public Set<ServerStatus> getServerStatus() {
    return serverStatus;
  }

  public int getWarningCount() {
    return warningCount;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return String.format(
        "OK response (affected rows: %d, insert id: %d, warning count: %d, message: '%s', server status: %s",
        affectedRows, insertId, warningCount, message, serverStatus.toString());
  }
}
