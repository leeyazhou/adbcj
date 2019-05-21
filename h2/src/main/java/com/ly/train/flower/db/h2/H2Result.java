package com.ly.train.flower.db.h2;

import java.util.List;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.support.DefaultResult;


public class H2Result extends DefaultResult {
  private final ResultSet result;

  public H2Result(ResultSet result, Long affectedRows, List<String> warnings) {
    super(affectedRows, warnings);
    this.result = result;
  }

  @Override
  public ResultSet getGeneratedKeys() {
    return result;
  }
}
