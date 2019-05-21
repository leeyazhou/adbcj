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

public final class ResultAndState {
  private final DecoderState newState;
  private final boolean waitForMoreInput;

  private ResultAndState(DecoderState newState, boolean waitForMoreInput) {
    this.newState = newState;
    this.waitForMoreInput = waitForMoreInput;
  }


  public DecoderState getNewState() {
    return newState;
  }

  public boolean isWaitingForMoreInput() {
    return waitForMoreInput;
  }

  public static ResultAndState newState(DecoderState newState) {
    return new ResultAndState(newState, false);
  }

  public static ResultAndState waitForMoreInput(DecoderState newState) {
    return new ResultAndState(newState, true);
  }
}
