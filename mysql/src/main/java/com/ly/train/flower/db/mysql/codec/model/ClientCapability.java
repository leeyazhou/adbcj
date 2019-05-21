/**
 * Copyright © 2019 yazhou.li (lee_yazhou@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
package com.ly.train.flower.db.mysql.codec.model;

/**
 * 客户端功能 https://github.com/google/mysql/blob/master/include/mysql_com.h
 * 
 * @author lee
 */
// TODO Document ClientCapabilities class
public enum ClientCapability {

  LONG_PASSWORD(1),

  FOUND_ROWS(2),

  LONG_COLUMN_FLAG(4),

  CONNECT_WITH_DB(8),

  NO_SCHEMA(16),

  COMPRESS(32),

  ODBC_CLIENT(64),

  LOCAL_FILES(128),

  IGNORE_SPACES(256),

  PROTOCOL_4_1(512),

  INTERACTIVE(1024),

  SSL(2048),

  IGNORE_SIGPIPE(4096),

  TRANSACTIONS(8192),

  /**
   * Old flag for 4.1 protocol
   */
  CLIENT_RESERVED(16384),

  /**
   * New 4.1 authentication
   */
  SECURE_CONNECTION(32768);

  private int code;

  private ClientCapability(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
