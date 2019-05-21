package org.adbcj.mysql.codec.packets.response;

import java.util.ArrayList;
import java.util.List;
import org.adbcj.mysql.codec.model.MysqlType;


public final class PreparedStatementResponse extends AbstractResponse {
  private final OKPreparedStatementResponse preparedStatement;
  private final List<MysqlType> parametersTypes;

  public PreparedStatementResponse(int packetLength, int packetNumber,
      OKPreparedStatementResponse preparedStatement) {
    this(packetLength, packetNumber, preparedStatement, new ArrayList<MysqlType>());
  }

  public PreparedStatementResponse(int packetLength, int packetNumber,
      OKPreparedStatementResponse preparedStatement, List<MysqlType> parametersTypes) {
    super(packetLength, packetNumber);
    this.preparedStatement = preparedStatement;
    this.parametersTypes = parametersTypes;
  }

  public OKPreparedStatementResponse getPreparedStatement() {
    return preparedStatement;
  }

  public int getHandlerId() {
    return preparedStatement.getHandlerId();
  }

  public List<MysqlType> getParametersTypes() {
    return parametersTypes;
  }

  public int getColumns() {
    return preparedStatement.getColumns();
  }

  public int getParams() {
    return preparedStatement.getParams();
  }
}
