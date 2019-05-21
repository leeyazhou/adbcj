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

public enum FieldFlag {
	NOT_NULL,
	PRI_KEY,
	UNIQUE_KEY,
	MULTIPLE_KEY,
	BLOB,
	UNSIGNED,
	ZEROFILL,
	BINARY,
	ENUM,
	AUTO_INCREMENT,
	TIMESTAMP,
	SET
}
