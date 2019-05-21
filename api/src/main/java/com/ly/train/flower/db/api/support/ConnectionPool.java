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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;


public final class ConnectionPool<TKey, VConnection> {

  private ConcurrentMap<TKey, ConcurrentLinkedDeque<VConnection>> keyToConnections = new ConcurrentHashMap<>();

  public VConnection tryAquire(TKey key) {
    ConcurrentLinkedDeque<VConnection> connections = keyToConnections.get(key);
    if (connections == null) {
      return null;
    }
    return connections.pollFirst();
  }

  public void release(TKey key, VConnection connection) {
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    if (connection == null) {
      throw new IllegalArgumentException("connection cannot be null");
    }

    ConcurrentLinkedDeque<VConnection> connections = keyToConnections.get(key);
    if (null == connections) {
      keyToConnections.putIfAbsent(key, new ConcurrentLinkedDeque<>());
      connections = keyToConnections.get(key);
    }

    connections.offer(connection);
  }
}
