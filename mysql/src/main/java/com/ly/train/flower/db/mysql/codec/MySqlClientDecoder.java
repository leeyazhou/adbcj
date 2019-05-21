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
package com.ly.train.flower.db.mysql.codec;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.mysql.codec.decoder.AbstractDecoder;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.packets.FailedToParseInputResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.AbstractResponse;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;
import io.netty.channel.Channel;

/**
 * Client stateful decoder. Being stateful, each client connection must have its
 * own decoder instance to function properly.
 *
 * @author leeyazhou
 */
public class MySqlClientDecoder {
  private static final Logger logger = LoggerFactory.getLogger(MySqlClientDecoder.class);
  private AbstractDecoder decoder;
  public MySqlClientDecoder(AbstractDecoder decoder) {
    this.decoder = decoder;
  }

  /**
   * Decodes a message from a MySql server.
   *
   *
   * @param input the {@code InputStream} from which to decode the message
   * @param block true if the decoder can block, false otherwise @return the
   *        decode message, null if the {@code block} is {@code} false and there
   *        is not enough data available to decode the message without blocking
   * @throws IOException thrown if an error occurs reading data from the
   *         inputstream
   */
  public AbstractResponse decode(InputStream input, Channel channel, boolean block) throws IOException {
    // If mark is not support and we can't block, throw an exception
    if (!input.markSupported() && !block) {
      throw new IllegalArgumentException("Non-blocking decoding requires an InputStream that supports marking");
    }
    // TODO This should be the max packet size - make this configurable
    input.mark(Integer.MAX_VALUE);
    AbstractResponse message = null;
    try {
      message = doDecode(input, channel, block);
    } finally {
      if (message == null) {
        input.reset();
      }
    }
    return message;
  }

  protected AbstractResponse doDecode(InputStream input, Channel channel, boolean block) throws IOException {
    // If we can't block, make sure there's enough data available to read
    if (!block) {
      if (input.available() < 3) {
        return null;
      }
    }
    // Read the packet length
    final int length = IOUtil.readUnsignedMediumInt(input);

    // If we can't block, make sure the stream has enough data
    if (!block) {
      // Make sure we have enough data for the packet length and the packet number
      if (input.available() < length + 1) {
        return null;
      }
    }
    final int packetNumber = IOUtil.safeRead(input);
    final BoundedInputStream inputStream = new BoundedInputStream(input, length);
    logger.debug("Decoding in state {}", decoder);
    final ResponseWrapper responseWrapper = decoder.decode(length, packetNumber, inputStream, channel);
    this.setDecoder(responseWrapper.getNewDecoder());
    final int rem = inputStream.getRemaining();
    if (rem > 0) {
      final String message =
          "Didn't read all input. Maybe this input belongs to a failed request. " + "Remaining bytes: " + rem;
      return new FailedToParseInputResponse(length, packetNumber, new IllegalStateException(message));
    }
    return responseWrapper.getResult();
  }

  /**
   * Sets the state, used for testing.
   */
  void setDecoder(AbstractDecoder newDecoder) {
    this.decoder = newDecoder;
  }
}
