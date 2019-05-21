/*
	This file is part of asyncdb.

	asyncdb is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	asyncdb is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with asyncdb.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2008  Mike Heath
 */
package com.ly.train.flower.db.mysql.codec.packets.response;

import java.util.Set;
import com.ly.train.flower.db.mysql.codec.model.ServerStatus;

public class EofResponse extends AbstractResponse {

  public enum Type {
    FIELD, ROW, STATEMENT,
  }

  private final int warnings;
  private final Set<ServerStatus> serverStatus;
  private final Type type;

  public EofResponse(int packetLength, int packetNumber, int warnings, Set<ServerStatus> serverStatus, Type type) {
    super(packetLength, packetNumber);
    this.warnings = warnings;
    this.serverStatus = serverStatus;
    this.type = type;
  }

  public int getWarnings() {
    return warnings;
  }

  public Set<ServerStatus> getServerStatus() {
    return serverStatus;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return "EofResponse{" + "type=" + type + ", warnings=" + warnings + ", serverStatus=" + serverStatus + '}';
  }
}
