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
 * 客户端功能
 * 
 * @author lee
 */
public enum ClientCapabilityExtend {

  /**
   * Enable/disable multi-stmt support
   */
  MULTI_STATEMENTS(1 << 16),

  /**
   * Enable/disable multi-results
   */
  MULTI_RESULTS(1 << 17),

  /**
   * Multi-results in PS-protocol
   */
  PS_MULTI_RESULTS(1 << 18);

  int code;

  private ClientCapabilityExtend(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
