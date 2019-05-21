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

import java.util.Map;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.DbException;

/**
 * Entry point to find a driver. The {@link com.ly.train.flower.db.api.ConnectionManagerProvider}
 * loads the ConnectionManagerFactory's via {@link java.util.ServiceLoader}.
 * Then checks if can handle the given protocol at hand
 */
public interface ConnectionManagerFactory {

  ConnectionManager createConnectionManager(String url, String username, String password,
      Map<String, String> properties) throws DbException;

  boolean canHandle(String protocol);

}
