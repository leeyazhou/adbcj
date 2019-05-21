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
 *
 * @author Mike Heath <mheath@apache.org>
 */
// TODO Document ServerStatus and what each bit means
public enum ServerStatus {
  IN_TRANSACTION(0x0001),

  AUTOCOMMIT(0x0002),
  // MORE_RESULTS_EXISTS(0x008),
  // MULTI_QUERY,
  // BAD_INDEX,
  // NO_INDEX,
  CURSOR_EXISTS(0x0040),

  LAST_ROW_SENT(0x0080),

  DATABASE_DROPPED(0x0100),

  NO_BACKSLASH_ESCAPES(0x0200),

  METADATA_CHANGED(0x0400);

  private int code;

  private ServerStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
