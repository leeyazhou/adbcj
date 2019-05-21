package com.ly.train.flower.db.mysql;

import com.ly.train.flower.db.api.*;
import com.ly.train.flower.db.api.support.CloseOnce;
import com.ly.train.flower.db.api.support.DbCompletableFuture;
import com.ly.train.flower.db.api.support.DefaultResultEventsHandler;
import com.ly.train.flower.db.api.support.DefaultResultSet;
import com.ly.train.flower.db.mysql.codec.packets.response.StatementPreparedEOFResponse;
import com.ly.train.flower.db.mysql.codec.util.MySqlRequestUtil;
import java.util.concurrent.CompletableFuture;


public class MySqlPreparedStatement implements PreparedQuery, PreparedUpdate {
  private final MySqlConnection connection;
  private final StatementPreparedEOFResponse statementInfo;
  private final CloseOnce closeFuture = new CloseOnce();

  public MySqlPreparedStatement(MySqlConnection connection, StatementPreparedEOFResponse statementInfo) {
    this.connection = connection;
    this.statementInfo = statementInfo;
  }

  public CompletableFuture execute(Object... params) {
    DbCompletableFuture<Result> future = new DbCompletableFuture<>();
    execute(future, params);
    return future;
  }

  @Override
  public void execute(DbCallback callback, Object... params) {
    executeWithCallback(new DefaultResultEventsHandler(), new DefaultResultSet(), callback, params);
  }

  @Override
  public <T> void executeWithCallback(ResultHandler<T> eventHandler, T accumulator, DbCallback<T> callback,
      Object... params) {
    connection.checkClosed();
    validateParameters(params);
    StackTraceElement[] entry = connection.strackTraces.captureStacktraceAtEntryPoint();
    connection.failIfQueueFull(MySqlRequestUtil.executePreparedQuery(connection, statementInfo, params, eventHandler,
        accumulator, callback, entry));
  }

  @Override
  public boolean isClosed() {
    return closeFuture.isClose();
  }


  @Override
  public void close(DbCallback<Void> callback) {
    closeFuture.requestClose(callback, () -> {
      if (connection.failIfQueueFull(MySqlRequestUtil.closeStatemeent(connection, statementInfo, (res, error) -> {
      }))) {
        closeFuture.didClose(null);
      }
    });
  }

  private void validateParameters(Object[] params) {
    if (isClosed()) {
      throw new IllegalStateException("Cannot execute closed statement");
    }
    if (params.length != statementInfo.getParametersTypes().size()) {
      throw new IllegalArgumentException("Expect " + statementInfo.getParametersTypes().size() + " paramenters "
          + "but got " + params.length + " parameters");
    }
  }
}
