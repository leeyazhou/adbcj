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
package com.ly.train.flower.db.mysql.codec.model;

/**
 *
 * @author leeyazhou
 */
// TODO Document ServerStatus and what each bit means
public enum ServerStatus {
  IN_TRANSACTION(0x0001),

  AUTOCOMMIT(0x0002),

  MORE_RESULTS_EXISTS(0x008),

  QUERY_NO_GOOD_INDEX_USED(0x010),

  QUERY_NO_INDEX_USED(0x020),

  CURSOR_EXISTS(0x0040),

  LAST_ROW_SENT(0x0080),

  DATABASE_DROPPED(0x0100),

  NO_BACKSLASH_ESCAPES(0x0200),

  METADATA_CHANGED(0x0400),

  QUERY_WAS_SLOW(0x0800),

  PS_OUT_PARAMS(0x1000),

  IN_TRANS_READONLY(0x2000);

  private int code;

  private ServerStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
