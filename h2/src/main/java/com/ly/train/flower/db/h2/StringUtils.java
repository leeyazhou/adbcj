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


final class StringUtils {
  private static final char[] HEX = "0123456789abcdef".toCharArray();

  /**
   * Convert a byte array to a hex encoded string.
   *
   * @param value the byte array
   * @return the hex encoded string
   */
  public static String convertBytesToHex(byte[] value) {
    return convertBytesToHex(value, value.length);
  }

  /**
   * Convert a byte array to a hex encoded string.
   *
   * @param value the byte array
   * @param len the number of bytes to encode
   * @return the hex encoded string
   */
  public static String convertBytesToHex(byte[] value, int len) {
    char[] buff = new char[len + len];
    char[] hex = HEX;
    for (int i = 0; i < len; i++) {
      int c = value[i] & 0xff;
      buff[i + i] = hex[c >> 4];
      buff[i + i + 1] = hex[c & 0xf];
    }
    return new String(buff);
  }
}
