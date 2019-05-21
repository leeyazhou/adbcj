/*
	This file is part of ADBCJ.

	ADBCJ is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	ADBCJ is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with ADBCJ.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2008  Mike Heath
 */
package org.adbcj.mysql.codec.packets.response;

import org.adbcj.mysql.codec.BoundedInputStream;
import org.adbcj.mysql.codec.model.ServerStatus;
import org.adbcj.mysql.codec.util.IOUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

public abstract class OkResponse extends AbstractResponse {

  protected OkResponse(int packetLength, int packetNumber) {
    super(packetLength, packetNumber);
  }

  public static RegularOK interpretAsRegularOk(int packetLength, int packetNumber, BoundedInputStream toParse)
      throws IOException {
    long affectedRows = IOUtil.readBinaryLengthEncoding(toParse);
    long insertId = IOUtil.readBinaryLengthEncoding(toParse);
    Set<ServerStatus> serverStatus = IOUtil.readEnumSetShort(toParse, ServerStatus.class);
    int warningCount = IOUtil.readUnsignedShort(toParse);
    String message = IOUtil.readFixedLengthString(toParse, toParse.getRemaining(), StandardCharsets.UTF_8);
    return new RegularOK(packetLength, packetNumber, affectedRows, insertId, serverStatus, warningCount, message);
  }

  public static PreparedStatementOK interpretAsPreparedStatement(int packetLength, int packetNumber,
      BoundedInputStream toParse) throws IOException {
    int handlerId = IOUtil.readInt(toParse);
    int columns = IOUtil.readShort(toParse);
    int params = IOUtil.readShort(toParse);
    @SuppressWarnings("unused")
    int filler = toParse.read();
    int warnings = IOUtil.readShort(toParse);
    return new PreparedStatementOK(packetLength, packetNumber, handlerId, columns, params, warnings);
  }

  public static class PreparedStatementOK extends OkResponse {

    private final int handlerId;
    private final int columns;
    private final int params;
    private final int warnings;

    public PreparedStatementOK(int packetLength, int packetNumber, int handlerId, int columns, int params,
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

  public static class RegularOK extends OkResponse {

    private final long affectedRows;
    private final long insertId;
    private final Set<ServerStatus> serverStatus;
    private final int warningCount;
    private final String message;

    public RegularOK(int length, int packetNumber, long affectedRows, long insertId, Set<ServerStatus> serverStatus,
        int warningCount, String message) {
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

}
