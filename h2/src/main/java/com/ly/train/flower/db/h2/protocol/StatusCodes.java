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
package com.ly.train.flower.db.h2.protocol;

import com.ly.train.flower.db.api.exception.DbException;


public enum StatusCodes {
  STATUS_ERROR(0), STATUS_OK(1);

  private final int statusValue;

  StatusCodes(int statusValue) {
    this.statusValue = statusValue;
  }

  public boolean isStatus(int status) {
    return this.statusValue == status;
  }

  /**
   * Expect this status or throw
   */
  public void expectStatusOrThrow(int status) {
    if (!isStatus(status)) {
      throw new DbException("Expected status: " + this + " but got: " + status);
    }
  }
}
