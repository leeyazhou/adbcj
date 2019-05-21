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
