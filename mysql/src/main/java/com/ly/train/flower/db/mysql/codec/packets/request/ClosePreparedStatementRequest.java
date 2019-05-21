package com.ly.train.flower.db.mysql.codec.packets.request;

import java.io.IOException;
import java.io.OutputStream;
import com.ly.train.flower.db.mysql.codec.packets.Command;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;


public class ClosePreparedStatementRequest extends CommandRequest {
    private final int statementId;

    public ClosePreparedStatementRequest(int statementId) {
        super(Command.STATEMENT_CLOSE);
        this.statementId = statementId;
    }


    @Override
    public boolean hasPayload() {
        return true;
    }
    @Override
    public int getLength() {
        return 1+4;
    }
    @Override
    protected void writePayLoad(OutputStream out) throws IOException {
        IOUtil.writeInt(out, statementId);
    }

    @Override
    public String toString() {
        return "ClosePreparedStatementRequest{" +
                "statementId=" + statementId +
                '}';
    }
}
