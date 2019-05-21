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

public class ResultSetResponse extends AbstractResponse {

  private final int fieldCount;
  private final Long extra;

  public ResultSetResponse(int length, int packetNumber, int fieldCount, Long extra) {
    super(length, packetNumber);
    this.fieldCount = fieldCount;
    this.extra = extra;
  }

  public int getFieldCount() {
    return fieldCount;
  }

  public Long getExtra() {
    return extra;
  }

}
