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

import org.adbcj.mysql.codec.exception.MysqlException;

public class ErrorResponse extends AbstractResponse {

	private final int errorNumber;
	private final String sqlState;
	private final String message;

	public ErrorResponse(int length, int packetNumber, int errorNumber, String sqlState, String message) {
		super(length, packetNumber);
		this.errorNumber = errorNumber;
		this.sqlState = sqlState;
		this.message = message;
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public String getSqlState() {
		return sqlState;
	}

	public String getMessage() {
		return message;
	}

    public MysqlException toException(StackTraceElement[] entry){
    	// Error format adjustment as mysql command reporting.
    	// @since 2017-09-01 little-pan
        return new MysqlException(toString(), null, entry);
    }
    
    @Override
    public String toString() {
    	final String format= "ERROR %d (%s): %s";
    	return (String.format(format, errorNumber, sqlState, message));
    }

}
