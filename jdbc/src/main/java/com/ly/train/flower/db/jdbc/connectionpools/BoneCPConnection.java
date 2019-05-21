package com.ly.train.flower.db.jdbc.connectionpools;

import com.jolbox.bonecp.BoneCP;
import com.ly.train.flower.db.jdbc.JDBCConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;


public class BoneCPConnection implements JDBCConnectionProvider {
  private final BoneCP connectionPool;

  public BoneCPConnection(BoneCP connectionPool) {
    this.connectionPool = connectionPool;
  }


  @Override
  public Connection getConnection() throws SQLException {
    return connectionPool.getConnection();
  }

  @Override
  public Connection getConnection(String user, String password) throws SQLException {
    throw new UnsupportedOperationException("Not yet supported for JDBC connection pool");
  }
}
