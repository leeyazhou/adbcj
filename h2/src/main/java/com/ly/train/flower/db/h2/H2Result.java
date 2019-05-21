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

import java.util.List;
import com.ly.train.flower.db.api.ResultSet;
import com.ly.train.flower.db.api.support.DefaultResult;


public class H2Result extends DefaultResult {
  private final ResultSet result;

  public H2Result(ResultSet result, Long affectedRows, List<String> warnings) {
    super(affectedRows, warnings);
    this.result = result;
  }

  @Override
  public ResultSet getGeneratedKeys() {
    return result;
  }
}
