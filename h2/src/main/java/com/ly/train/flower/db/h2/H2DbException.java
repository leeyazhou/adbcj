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
package com.ly.train.flower.db.h2;

import com.ly.train.flower.db.api.DbException;


public class H2DbException extends DbException {
  public final String sqlstate;
  public final String message;
  public final String sql;
  public final int errorCode;
  private final String stackTrace;

  public H2DbException(String sqlstate, String message, String sql, int errorCode, String stackTrace) {
    super(message);
    this.sqlstate = sqlstate;
    this.message = message;
    this.sql = sql;
    this.errorCode = errorCode;
    this.stackTrace = stackTrace;
  }

  public H2DbException(String sqlstate, String message, String sql, int errorCode, String stackTrace,
      StackTraceElement[] entry) {
    super(message, null, entry);
    this.sqlstate = sqlstate;
    this.message = message;
    this.sql = sql;
    this.errorCode = errorCode;
    this.stackTrace = stackTrace;
  }

  public static H2DbException create(String sqlstate, String message, String sql, int errorCode, String stackTrace,
      StackTraceElement[] entry) {
    if (entry == null) {
      return new H2DbException(sqlstate, message, sql, errorCode, stackTrace);
    } else {
      return new H2DbException(sqlstate, message, sql, errorCode, stackTrace, entry);
    }
  }

}
