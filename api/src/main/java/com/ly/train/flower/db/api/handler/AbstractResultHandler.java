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
package com.ly.train.flower.db.api.handler;

import com.ly.train.flower.db.api.Field;
import com.ly.train.flower.db.api.Value;

/**
 * An empty implementation of the {@link ResultHandler} interface to avoid
 * implementing each and every method.
 */
public abstract class AbstractResultHandler<T> implements ResultHandler<T> {
  @Override
  public void startFields(T accumulator) {}

  @Override
  public void field(Field field, T accumulator) {}

  @Override
  public void endFields(T accumulator) {}

  @Override
  public void startResults(T accumulator) {}

  @Override
  public void startRow(T accumulator) {}

  @Override
  public void value(Value value, T accumulator) {}

  @Override
  public void endRow(T accumulator) {}

  @Override
  public void endResults(T accumulator) {}
}
