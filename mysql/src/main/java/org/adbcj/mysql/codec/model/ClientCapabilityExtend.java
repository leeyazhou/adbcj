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
package org.adbcj.mysql.codec.model;

/**
 * 客户端功能
 * 
 * @author lee
 */
public enum ClientCapabilityExtend {

  /**
   * Enable/disable multi-stmt support
   */
  MULTI_STATEMENTS(1 << 16),

  /**
   * Enable/disable multi-results
   */
  MULTI_RESULTS(1 << 17),

  /**
   * Multi-results in PS-protocol
   */
  PS_MULTI_RESULTS(1 << 18);

  int code;

  private ClientCapabilityExtend(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
