package com.ly.train.flower.db.jdbc;

import java.sql.SQLException;
import java.util.List;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.api.DbException;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.support.DefaultResult;
import com.ly.train.flower.db.api.support.DefaultResultEventsHandler;
import com.ly.train.flower.db.api.support.DefaultResultSet;


class JDBCResult extends DefaultResult {
  private final ResultSet generatedKeys;

  public JDBCResult(long affectedRows, List<String> warnings, java.sql.ResultSet generatedKeys,
      DbCallback<DefaultResultSet> callback, StackTraceElement[] entry) {
    super(affectedRows, warnings);
    DefaultResultSet resultSet = new DefaultResultSet();
    try {
      ResultSetCopier.fillResultSet(generatedKeys, new DefaultResultEventsHandler(), resultSet);
      this.generatedKeys = resultSet;
    } catch (SQLException e) {
      throw DbException.wrap(e);
    }
  }

  @Override
  public ResultSet getGeneratedKeys() {
    return generatedKeys;
  }
}
