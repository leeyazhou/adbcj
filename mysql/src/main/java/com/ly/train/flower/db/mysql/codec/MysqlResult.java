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
package com.ly.train.flower.db.mysql.codec;

import com.ly.train.flower.db.api.Field;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.support.DefaultResult;
import com.ly.train.flower.db.api.support.DefaultResultSet;
import com.ly.train.flower.db.api.support.DefaultRow;
import com.ly.train.flower.db.api.support.DefaultValue;
import com.ly.train.flower.db.mysql.codec.model.FieldFlag;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.model.MysqlType;
import java.util.EnumSet;
import java.util.List;


public class MysqlResult extends DefaultResult {
    private final ResultSet generatedKeys;

    public MysqlResult(Long affectedRows, List<String> warnings, long autoKey) {
        super(affectedRows, warnings);
        DefaultResultSet generatedKeys = new DefaultResultSet();
        Field autoIdField = new MysqlField(0,"","","","",
                MysqlType.LONGLONG,"GENERATEDID","GENERATEDID",
                0,
                0,
                MysqlCharacterSet.UTF8_UNICODE_CI,
                8,
                EnumSet.of(FieldFlag.NOT_NULL),0);
        generatedKeys.addField(autoIdField);
        for(int i=0;i<affectedRows;i++){
            DefaultRow row = new DefaultRow(generatedKeys,new DefaultValue(autoKey+i));
            generatedKeys.addResult(row);
        }
        this.generatedKeys = generatedKeys;
    }

    @Override
    public ResultSet getGeneratedKeys() {
        return generatedKeys;
    }
}
