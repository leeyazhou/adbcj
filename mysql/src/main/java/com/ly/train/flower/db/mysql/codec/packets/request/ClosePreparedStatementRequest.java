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
