/*
	This file is part of ADBCJ.

	ADBCJ is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	ADBCJ is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with ADBCJ.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2008  Mike Heath
 */
package org.adbcj.mysql.codec;

import org.adbcj.mysql.codec.decoder.AbstractDecoder;
import org.adbcj.mysql.codec.model.ResponseWrapper;
import org.adbcj.mysql.codec.packets.FailedToParseInputResponse;
import org.adbcj.mysql.codec.packets.response.AbstractResponse;
import org.adbcj.mysql.codec.util.IOUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Client stateful decoder. Being stateful, each client connection must have its
 * own decoder instance to function properly.
 *
 * @author Mike Heath <mheath@apache.org>
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
    // Dump packet for debug
    // @since 2017-08-29 little-pan
    final boolean debug;
    if (debug = logger.isDebugEnabled()) {
      ByteBuf dumpBuf = null;
      try {
        input.mark(Integer.MAX_VALUE);
        dumpBuf = channel.alloc().buffer(length + 4).writeMedium(length).writeByte(packetNumber);
        dumpBuf.writeBytes(input, length);
        logger.debug("Received packet: \n{}", ByteBufUtil.prettyHexDump(dumpBuf));
      } finally {
        if (dumpBuf != null) {
          dumpBuf.release();
        }
        input.reset();
      }
    }
    // Dump packet for debug
    final BoundedInputStream inputStream = new BoundedInputStream(input, length);
    logger.trace("Decoding in state {}", decoder);
    final ResponseWrapper stateAndResult = decoder.decode(length, packetNumber, inputStream, channel);
    final AbstractDecoder nextDecoder = stateAndResult.getNewDecoder();
    if (debug && (decoder != nextDecoder)) {
      logger.debug("New state of the decoding is: {}", nextDecoder);
    }
    this.decoder = nextDecoder;
    final int rem = inputStream.getRemaining();
    if (rem > 0) {
      final String message =
          "Didn't read all input. Maybe this input belongs to a failed request. " + "Remaining bytes: " + rem;
      return new FailedToParseInputResponse(length, packetNumber, new IllegalStateException(message));
    }
    return stateAndResult.getResult();

  }



  /**
   * Sets the state, used for testing.
   */
  void setState(AbstractDecoder state) {
    this.decoder = state;
  }
}
