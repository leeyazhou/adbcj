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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


final class MathUtils {
  /**
   * The secure random object.
   */
  static SecureRandom secureRandom;

  static {
    try {
      secureRandom = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException e) {
      warn("SecureRandom", e);
      secureRandom = new SecureRandom();
    }
  }

  /**
   * Get a number of cryptographically secure pseudo random bytes.
   *
   * @param len the number of bytes
   * @return the random bytes
   */
  static byte[] secureRandomBytes(int len) {
    if (len <= 0) {
      len = 1;
    }
    byte[] buff = new byte[len];
    secureRandom.nextBytes(buff);
    return buff;
  }

  /**
   * Print a message to system output if there was a problem initializing the
   * random number generator.
   *
   * @param s the message to print
   * @param t the stack trace
   */
  static void warn(String s, Throwable t) {
    // not a fatal problem, but maybe reduced security
    System.out.println("Warning: " + s);
    if (t != null) {
      t.printStackTrace();
    }
  }


}
