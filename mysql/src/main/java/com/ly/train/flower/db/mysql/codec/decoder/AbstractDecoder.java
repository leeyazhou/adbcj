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
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.mysql.codec.BoundedInputStream;
import com.ly.train.flower.db.mysql.codec.decoder.util.SandboxDbCallback;
import com.ly.train.flower.db.mysql.codec.model.ResponseWrapper;
import com.ly.train.flower.db.mysql.codec.model.ServerStatus;
import com.ly.train.flower.db.mysql.codec.packets.response.AbstractResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.EofResponse;
import com.ly.train.flower.db.mysql.codec.packets.response.ErrorResponse;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;
import io.netty.channel.Channel;

public abstract class AbstractDecoder {
  public static final int RESPONSE_ERROR = 0xff;
  public static final int RESPONSE_EOF = 0xfe;
  public static final int RESPONSE_OK = 0x00;

  /**
   * 解码器
   * 
   * @param length
   * @param packetNumber
   * @param in
   * @param channel
   * @return
   * @throws IOException
   */
  public abstract ResponseWrapper decode(int length, int packetNumber, BoundedInputStream in, Channel channel)
      throws IOException;

  /**
   * 包装 新解码器和 结果
   * 
   * @param newDecoder 新解码器
   * @param response 响应结果
   * @return
   */
  public ResponseWrapper resultWrapper(AbstractDecoder newDecoder, AbstractResponse response) {
    return new ResponseWrapper(newDecoder, response);
  }

  protected EofResponse decodeEofResponse(InputStream in, int length, int packetNumber, EofResponse.Type type)
      throws IOException {
    int warnings = IOUtil.readUnsignedShort(in);
    Set<ServerStatus> serverStatus = IOUtil.readEnumSetShort(in, ServerStatus.class);

    return new EofResponse(length, packetNumber, warnings, serverStatus, type);
  }

  public ErrorResponse decodeErrorResponse(BoundedInputStream in, int length, int packetNumber) throws IOException {
    // The Payload of an ERR Packet
    // Type Name Description
    // -----------------------------------------------------------
    // int<1> header 0xFF ERR packet header
    // int<2> error_code error-code
    // if capabilities & CLIENT_PROTOCOL_41 {
    // string[1] sql_state_marker # marker of the SQL state
    // string[5] sql_state SQL state
    // }
    // string<EOF> error_message human readable error message
    final int errorNumber = IOUtil.readUnsignedShort(in);
    final boolean hasMarker;
    in.mark(Integer.MAX_VALUE);
    try {
      hasMarker = ('#' == in.read());
    } finally {
      in.reset();
    }
    final Charset CHARSET = StandardCharsets.UTF_8;
    final String sqlState, message;
    if (hasMarker) {
      in.read(); // Throw away sqlstate marker
      // fixbug: sql_state string[5] as null-terming-string.
      // @since 2017-08-27 little-pan
      // String sqlState = IoUtils.readNullTerminatedString(in, CHARSET);
      sqlState = IOUtil.readFixedLengthString(in, 5, CHARSET);
      message = IOUtil.readNullTerminatedString(in, CHARSET);
    } else {
      // Prev-4.1's message, still can be output
      // in newer versions(e.g 'Too many connections')
      // @since 2017-09-01 little-pan
      sqlState = "HY000";
      message = IOUtil.readFixedLengthString(in, length - 3, CHARSET);
    }
    return new ErrorResponse(length, packetNumber, errorNumber, sqlState, message);
  }

  /**
   * Sand-boxing the DbCallback for protecting asyncdb kernel from being destroyed
   * when the exception occurs in user DbCallback.onComplete() code.
   *
   * @param callback the database callback
   * @return the sand-boxed callback
   * @since 2017-09-02 little-pan
   */
  public <T> DbCallback<T> sandboxCallback(final DbCallback<T> callback) {
    if (callback.getClass() == SandboxDbCallback.class) {
      return callback;
    }
    return (new SandboxDbCallback<T>(callback));
  }


}
