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
package com.ly.train.flower.db.mysql.codec.decoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.MySqlPreparedStatement;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.model.MysqlType;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.response.EofResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.PreparedStatementResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.StatementPreparedEOFResponse;
import io.netty.channel.Channel;


abstract class FinishPrepareStatementDecoder extends AbstractDecoder {

  protected final PreparedStatementResponse statement;
  protected final DbCallback<MySqlPreparedStatement> callback;
  protected final MySqlConnection connection;

  FinishPrepareStatementDecoder(MySqlConnection connection, PreparedStatementResponse statement,
      DbCallback<MySqlPreparedStatement> callback) {
    this.statement = statement;
    this.callback = sandboxCallback(callback);
    this.connection = connection;
  }

  protected void readAllAndIgnore(BoundedInputStream in) throws IOException {
    in.readFully(new byte[in.getRemaining()]);
  }

  public static AbstractDecoder create(MySqlConnection connection, PreparedStatementResponse statement,
      DbCallback<MySqlPreparedStatement> toComplete) {
    if (statement.getParams() > 0) {
      return new ReadParametersDecoder(connection, statement.getParams(), statement, toComplete);
    } else if (statement.getColumns() > 0) {
      return new ReadColumnsDecoder(connection, statement.getColumns(), statement, toComplete);
    } else {
      return new AcceptNextResponseDecoder(connection);
    }
  }

  private static class ReadParametersDecoder extends FinishPrepareStatementDecoder {
    private final int parametersToParse;

    public ReadParametersDecoder(MySqlConnection connection, int parametersToParse, PreparedStatementResponse statement,
        DbCallback<MySqlPreparedStatement> callback) {
      super(connection, statement, callback);

      this.parametersToParse = parametersToParse;
    }

    @Override
    public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
        throws IOException {
      int typesCount = statement.getParametersTypes().size();
      MysqlType newType = FieldDecodingStateDecoder.parseField(in, typesCount).getMysqlType();
      List<MysqlType> types = new ArrayList<MysqlType>(typesCount + 1);
      types.addAll(statement.getParametersTypes());
      types.add(newType);
      PreparedStatementResponse newStatement =
          new PreparedStatementResponse(length, packetNumber, statement.getPreparedStatement(), types);
      int restOfParams = parametersToParse - 1;
      if (restOfParams > 0) {
        return resultWrapper(new ReadParametersDecoder(connection, restOfParams, newStatement, callback), statement);
      } else {
        return resultWrapper(new EofAndColumnsDecoder(connection, newStatement, callback), statement);
      }
    }

    @Override
    public String toString() {
      return "PREPARED-STATEMENT-READ-PARAMETERS";
    }
  }

  private static class EofAndColumnsDecoder extends FinishPrepareStatementDecoder {

    public EofAndColumnsDecoder(MySqlConnection connection, PreparedStatementResponse statement,
        DbCallback<MySqlPreparedStatement> toComplete) {
      super(connection, statement, toComplete);
    }

    @Override
    public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
        throws IOException {
      if (in.read() == RESPONSE_EOF) {
        EofResponse eof = decodeEofResponse(in, length, packetNumber, EofResponse.Type.STATEMENT);
        if (statement.getColumns() == 0) {
          final StatementPreparedEOFResponse preparedEOF = new StatementPreparedEOFResponse(packetNumber, packetNumber, statement);
          callback.onComplete(new MySqlPreparedStatement(connection, preparedEOF), null);
          return resultWrapper(new AcceptNextResponseDecoder(connection), preparedEOF);
        } else {
          return resultWrapper(new ReadColumnsDecoder(connection, statement.getColumns(), statement, callback), statement);
        }
      } else {
        throw new IllegalStateException("Did not expect a EOF from the server");
      }
    }

    @Override
    public String toString() {
      return "PREPARED-STATEMENT-COLUMNS-EOF";
    }
  }

  private static class ReadColumnsDecoder extends FinishPrepareStatementDecoder {
    private final int restOfColumns;

    public ReadColumnsDecoder(MySqlConnection connection, int restOfColumns, PreparedStatementResponse statement,
        DbCallback<MySqlPreparedStatement> callback) {
      super(connection, statement, callback);
      this.restOfColumns = restOfColumns;
    }

    @Override
    public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
        throws IOException {
      readAllAndIgnore(in);
      int restOfParams = restOfColumns - 1;
      if (restOfParams > 0) {
        return resultWrapper(new ReadColumnsDecoder(connection, restOfParams, statement, callback), statement);
      } else {
        return resultWrapper(new EofStatementDecoder(connection, statement, callback), statement);
      }
    }

    @Override
    public String toString() {
      return "PREPARED-STATEMENT-READ-COLUMNS";
    }
  }

  private static class EofStatementDecoder extends FinishPrepareStatementDecoder {

    public EofStatementDecoder(MySqlConnection connection, PreparedStatementResponse statement,
        DbCallback<MySqlPreparedStatement> toComplete) {
      super(connection, statement, toComplete);
    }

    @Override
    public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
        throws IOException {
      if (in.read() == RESPONSE_EOF) {
        EofResponse eof = decodeEofResponse(in, length, packetNumber, EofResponse.Type.STATEMENT);

        final StatementPreparedEOFResponse preparedEOF = new StatementPreparedEOFResponse(packetNumber, packetNumber, statement);
        callback.onComplete(new MySqlPreparedStatement(connection, preparedEOF), null);
        return resultWrapper(new AcceptNextResponseDecoder(connection), preparedEOF);
      } else {
        throw new IllegalStateException("Did not expect a EOF from the server");
      }
    }
  }
}
