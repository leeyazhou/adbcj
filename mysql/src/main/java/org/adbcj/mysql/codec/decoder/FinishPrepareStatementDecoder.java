package org.adbcj.mysql.codec.decoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.adbcj.DbCallback;
import org.adbcj.mysql.MySqlConnection;
import org.adbcj.mysql.MySqlPreparedStatement;
import org.adbcj.mysql.codec.BoundedInputStream;
import org.adbcj.mysql.codec.model.MysqlType;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.response.EofResponse;
import org.adbcj.mysql.codec.packets.response.PreparedStatementToBuildResponse;
import org.adbcj.mysql.codec.packets.response.StatementPreparedEOFResponse;
import io.netty.channel.Channel;


abstract class FinishPrepareStatementDecoder extends AbstractDecoder {

  protected final PreparedStatementToBuildResponse statement;
  protected final DbCallback<MySqlPreparedStatement> callback;
  protected final MySqlConnection connection;

  FinishPrepareStatementDecoder(MySqlConnection connection, PreparedStatementToBuildResponse statement,
      DbCallback<MySqlPreparedStatement> callback) {
    this.statement = statement;
    this.callback = sandboxCallback(callback);
    this.connection = connection;
  }

  protected void readAllAndIgnore(BoundedInputStream in) throws IOException {
    in.readFully(new byte[in.getRemaining()]);
  }

  public static AbstractDecoder create(MySqlConnection connection, PreparedStatementToBuildResponse statement,
      DbCallback<MySqlPreparedStatement> toComplete) {
    if (statement.getParams() > 0) {
      return new ReadParameters(connection, statement.getParams(), statement, toComplete);
    } else if (statement.getColumns() > 0) {
      return new ReadColumns(connection, statement.getColumns(), statement, toComplete);
    } else {
      return new AcceptNextResponseDecoder(connection);
    }
  }

  private static class ReadParameters extends FinishPrepareStatementDecoder {
    private final int parametersToParse;

    public ReadParameters(MySqlConnection connection, int parametersToParse, PreparedStatementToBuildResponse statement,
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
      PreparedStatementToBuildResponse newStatement =
          new PreparedStatementToBuildResponse(length, packetNumber, statement.getPreparedStatement(), types);
      int restOfParams = parametersToParse - 1;
      if (restOfParams > 0) {
        return result(new ReadParameters(connection, restOfParams, newStatement, callback), statement);
      } else {
        return result(new EofAndColumns(connection, newStatement, callback), statement);
      }
    }

    @Override
    public String toString() {
      return "PREPARED-STATEMENT-READ-PARAMETERS";
    }
  }

  private static class EofAndColumns extends FinishPrepareStatementDecoder {

    public EofAndColumns(MySqlConnection connection, PreparedStatementToBuildResponse statement,
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
          return result(new AcceptNextResponseDecoder(connection), preparedEOF);
        } else {
          return result(new ReadColumns(connection, statement.getColumns(), statement, callback), statement);
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

  private static class ReadColumns extends FinishPrepareStatementDecoder {
    private final int restOfColumns;

    public ReadColumns(MySqlConnection connection, int restOfColumns, PreparedStatementToBuildResponse statement,
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
        return result(new ReadColumns(connection, restOfParams, statement, callback), statement);
      } else {
        return result(new EofStatement(connection, statement, callback), statement);
      }
    }

    @Override
    public String toString() {
      return "PREPARED-STATEMENT-READ-COLUMNS";
    }
  }

  private static class EofStatement extends FinishPrepareStatementDecoder {

    public EofStatement(MySqlConnection connection, PreparedStatementToBuildResponse statement,
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
        return result(new AcceptNextResponseDecoder(connection), preparedEOF);
      } else {
        throw new IllegalStateException("Did not expect a EOF from the server");
      }
    }
  }
}
