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
package com.ly.train.flower.db.api.support;


public final class SizeConstants {
  public static final int CHAR_SIZE = 2;
  public static final int INT_SIZE = 4;
  public static final int BYTE_SIZE = 1;
  public static final int LONG_SIZE = 8;
  public static final int DOUBLE_SIZE = 8;
  public static final int BOOLEAN_SIZE = 1;

  private SizeConstants() {}

  public static int sizeOf(boolean param) {
    return BOOLEAN_SIZE;
  }

  public static int sizeOf(int param) {
    return INT_SIZE;
  }

  public static int sizeOf(long param) {
    return LONG_SIZE;
  }

  public static int sizeOf(String param) {
    return INT_SIZE + (null == param ? 0 : CHAR_SIZE * param.length());
  }

  public static int lengthOfString(String param) {
    return CHAR_SIZE * param.length();
  }
}
