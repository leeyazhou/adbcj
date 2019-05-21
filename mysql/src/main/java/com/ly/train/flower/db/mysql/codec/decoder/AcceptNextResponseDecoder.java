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
package com.ly.train.flower.db.mysql.codec.decoder;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.model.MySqlRequest;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import io.netty.channel.Channel;


public class AcceptNextResponseDecoder extends AbstractDecoder {
  private static final Logger logger = LoggerFactory.getLogger(AcceptNextResponseDecoder.class);
  private final MySqlConnection connection;

  public AcceptNextResponseDecoder(MySqlConnection connection) {
    this.connection = connection;
  }

  @Override
  public ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel) throws IOException {
    final MySqlRequest<?> request = connection.dequeRequest();
    if (logger.isDebugEnabled()) {
      logger.debug("Start parsing request: {}", request);
    }
    return request.getDecoder().decode(length, packetNumber, in, channel);
  }
}
