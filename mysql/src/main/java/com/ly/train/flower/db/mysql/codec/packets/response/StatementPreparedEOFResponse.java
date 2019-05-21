package com.ly.train.flower.db.mysql.codec.packets.response;

import java.util.List;
import com.ly.train.flower.db.mysql.codec.model.MysqlType;

public final class StatementPreparedEOFResponse extends AbstractResponse {

    private final int handlerId;
    private final int colums;
    private List<MysqlType> parametersTypes;

    public StatementPreparedEOFResponse(int packetLength, int packetNumber, PreparedStatementResponse preparedStatement) {
        super(packetLength, packetNumber);
        this.handlerId = preparedStatement.getHandlerId();
        this.colums = preparedStatement.getColumns();
        this.parametersTypes = preparedStatement.getParametersTypes();
    }


    public int getHandlerId() {
        return handlerId;
    }

    public int getColumns() {
        return colums;
    }

    public List<MysqlType> getParametersTypes() {
        return parametersTypes;
    }
}
