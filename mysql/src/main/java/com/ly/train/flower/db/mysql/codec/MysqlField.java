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
package com.ly.train.flower.db.mysql.codec;


import com.ly.train.flower.db.api.support.DefaultField;
import com.ly.train.flower.db.mysql.codec.model.FieldFlag;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.model.MysqlType;
import java.util.Set;

// TODO Add support for 'ZEROFILL' when doing toString on integer Value objects
public class MysqlField extends DefaultField {

	private final MysqlType mysqlType;
	private final MysqlCharacterSet characterSet;
	private final long length;
	private final Set<FieldFlag> flags;
	private final long fieldDefault;

	public MysqlField(
			int index,
			String catalogName,
			String schemaName,
			String tableLabel,
			String tableName,
			MysqlType mysqlType,
			String columnLabel,
			String columnName,
			int precision,
			int decimals,
			MysqlCharacterSet characterSet,
			long length,
			Set<FieldFlag> flags,
			long fieldDefault) {

		super(
				index,
				catalogName,
				schemaName,
				tableLabel,
				tableName,
				mysqlType.getType(),
				columnLabel,
				columnName,
				precision,
				decimals,
				flags.contains(FieldFlag.AUTO_INCREMENT),
				mysqlType.isCaseSensitive(),
				mysqlType.isCurrency(),
				!flags.contains(FieldFlag.NOT_NULL),
				true, // TODO Determine how to handle currency flag for MySQL driver
				!flags.contains(FieldFlag.UNSIGNED),
				mysqlType.getClassName());
		this.mysqlType = mysqlType;
		this.characterSet = characterSet;
		this.length = length;
		this.flags = flags;
		this.fieldDefault = fieldDefault;
	}

	public MysqlType getMysqlType() {
		return mysqlType;
	}

	public MysqlCharacterSet getCharacterSet() {
		return characterSet;
	}

	public long getLength() {
		return length;
	}

	public Set<FieldFlag> getFlags() {
		return flags;
	}

	public long getFieldDefault() {
		return fieldDefault;
	}

	@Override
	public String toString() {
		return getColumnType() + " (" + mysqlType + ")";
	}

}
