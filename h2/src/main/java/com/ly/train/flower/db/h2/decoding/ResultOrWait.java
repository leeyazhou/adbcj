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
package com.ly.train.flower.db.h2.decoding;


public final class ResultOrWait<T> {
  public final T result;
  public final boolean couldReadResult;

  public final static ResultOrWait WaitLonger = new ResultOrWait(null, false);
  public final static ResultOrWait Start = new ResultOrWait(null, true);


  ResultOrWait(T result, boolean couldReadResult) {
    this.result = result;
    this.couldReadResult = couldReadResult;
  }

  public static <T> ResultOrWait<T> result(T data) {
    return new ResultOrWait<T>(data, true);
  }

  @Override
  public String toString() {
    if (couldReadResult) {
      return String.valueOf(result);
    } else {
      return "{No Result}";
    }
  }
}
