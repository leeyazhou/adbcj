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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashMap;
import org.junit.Test;
import org.testng.Assert;
import com.ly.train.flower.db.api.Configuration;
import com.ly.train.flower.db.api.support.DbCompletableFuture;
import com.ly.train.flower.db.api.support.LoginCredentials;
import com.ly.train.flower.db.api.support.stacktracing.StackTracingOptions;
import com.ly.train.flower.db.mysql.MySqlConnection;
import com.ly.train.flower.db.mysql.MysqlConnectionManager;
import com.ly.train.flower.db.mysql.codec.decoder.HandshakeDecoder;
import com.ly.train.flower.db.mysql.codec.model.ClientCapability;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.model.ServerStatus;
import com.ly.train.flower.db.mysql.codec.packets.response.HandshakeResponse;
import com.ly.train.flower.db.mysql.netty.NettyChannel;
import io.netty.channel.embedded.EmbeddedChannel;

public class GreetingDecodeTest {

  // Length 64
  // Packet Number 0
  // Protocol 10
  // Version: 5.0.51a-3ubuntu5.1
  // Thread ID: 831
  // Salt: 5863675b7149585f
  // Server capabilities: Long column flag, Connect with Database, Can use
  // compression, Speaks 4.1, Knows about transactions, can do 4.1 authentication
  // Charset: latin_swedish_ci (8)
  // Server Status: AUTO_COMMIT
  // Salt2: 5569335f645a755f5c317c41
  private byte[] GREETING1 = new byte[] {(byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a, (byte) 0x35,
      (byte) 0x2e, (byte) 0x30, (byte) 0x2e, (byte) 0x35, (byte) 0x31, (byte) 0x61, (byte) 0x2d, (byte) 0x33,
      (byte) 0x75, (byte) 0x62, (byte) 0x75, (byte) 0x6e, (byte) 0x74, (byte) 0x75, (byte) 0x35, (byte) 0x2e,
      (byte) 0x31, (byte) 0x00, (byte) 0x3f, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x58, (byte) 0x63,
      (byte) 0x67, (byte) 0x5b, (byte) 0x71, (byte) 0x49, (byte) 0x58, (byte) 0x5f, (byte) 0x00, (byte) 0x2c,
      (byte) 0xa2, (byte) 0x08, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x55, (byte) 0x69, (byte) 0x33, (byte) 0x5f, (byte) 0x64, (byte) 0x5a, (byte) 0x75,
      (byte) 0x5f, (byte) 0x5c, (byte) 0x31, (byte) 0x7c, (byte) 0x41, (byte) 0x00};

  private final LoginCredentials login = new LoginCredentials("sa", "sa", "test");


  @Test
  public void decodeGreeting1() throws IOException {
    InputStream in = new ByteArrayInputStream(GREETING1);
    MySqlClientDecoder decoder =
        new MySqlClientDecoder(new HandshakeDecoder(new DbCompletableFuture<>(), null, createMockConnection()));
    HandshakeResponse greeting = castToServerGreeting(in, decoder);

    Assert.assertEquals(greeting.getPacketLength(), 64);
    Assert.assertEquals(greeting.getPacketNumber(), 0);
    Assert.assertEquals(greeting.getProtocol(), 10);
    Assert.assertEquals(greeting.getVersion(), "5.0.51a-3ubuntu5.1");
    Assert.assertEquals(greeting.getThreadId(), 831);
    Assert.assertEquals(greeting.getSalt(),
        new byte[] {(byte) 0x58, (byte) 0x63, (byte) 0x67, (byte) 0x5b, (byte) 0x71, (byte) 0x49, (byte) 0x58,
            (byte) 0x5f, (byte) 0x55, (byte) 0x69, (byte) 0x33, (byte) 0x5f, (byte) 0x64, (byte) 0x5a, (byte) 0x75,
            (byte) 0x5f, (byte) 0x5c, (byte) 0x31, (byte) 0x7c, (byte) 0x41});
    Assert.assertEquals(greeting.getServerCapabilities(),
        EnumSet.of(ClientCapability.LONG_COLUMN_FLAG, ClientCapability.CONNECT_WITH_DB, ClientCapability.COMPRESS,
            ClientCapability.PROTOCOL_4_1, ClientCapability.TRANSACTIONS, ClientCapability.SECURE_CONNECTION));
    Assert.assertEquals(greeting.getCharacterSet(), MysqlCharacterSet.LATIN1_SWEDISH_CI);
    Assert.assertEquals(greeting.getServerStatus(), EnumSet.of(ServerStatus.AUTOCOMMIT));
  }

  // Length 74
  // Packet Number 0
  // Protocol 10
  // Version: 5.0.38-Ubuntu_0ubuntu1.4-log
  // Thread ID: 41344
  // Salt: 725a4d53395f5671
  // Server capabilities: Long column flag, Connect with Database, Can use
  // compression, Speaks 4.1, Knows about transactions, can do 4.1 authentication
  // Charset: latin_swedish_ci (8)
  // Server Status: AUTO_COMMIT
  // Salt2: 58417c6d314751433b3d625300
  private byte[] GREETING2 = new byte[] {(byte) 0x4a, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a, (byte) 0x35,
      (byte) 0x2e, (byte) 0x30, (byte) 0x2e, (byte) 0x33, (byte) 0x38, (byte) 0x2d, (byte) 0x55, (byte) 0x62,
      (byte) 0x75, (byte) 0x6e, (byte) 0x74, (byte) 0x75, (byte) 0x5f, (byte) 0x30, (byte) 0x75, (byte) 0x62,
      (byte) 0x75, (byte) 0x6e, (byte) 0x74, (byte) 0x75, (byte) 0x31, (byte) 0x2e, (byte) 0x34, (byte) 0x2d,
      (byte) 0x6c, (byte) 0x6f, (byte) 0x67, (byte) 0x00, (byte) 0x80, (byte) 0xa1, (byte) 0x00, (byte) 0x00,
      (byte) 0x72, (byte) 0x5a, (byte) 0x4d, (byte) 0x53, (byte) 0x39, (byte) 0x5f, (byte) 0x56, (byte) 0x71,
      (byte) 0x00, (byte) 0x2c, (byte) 0xa2, (byte) 0x08, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x58, (byte) 0x41, (byte) 0x7c, (byte) 0x6d, (byte) 0x31,
      (byte) 0x47, (byte) 0x51, (byte) 0x43, (byte) 0x3b, (byte) 0x3d, (byte) 0x62, (byte) 0x53, (byte) 0x00};

  @Test
  public void decodeGreeting2() throws IOException {
    InputStream in = new ByteArrayInputStream(GREETING2);
    MySqlClientDecoder decoder =
        new MySqlClientDecoder(new HandshakeDecoder(new DbCompletableFuture<>(), null, createMockConnection()));
    HandshakeResponse greeting = castToServerGreeting(in, decoder);

    Assert.assertEquals(greeting.getPacketLength(), 74);
    Assert.assertEquals(greeting.getPacketNumber(), 0);
    Assert.assertEquals(greeting.getProtocol(), 10);
    Assert.assertEquals(greeting.getVersion(), "5.0.38-Ubuntu_0ubuntu1.4-log");
    Assert.assertEquals(greeting.getThreadId(), 41344);
    Assert.assertEquals(greeting.getSalt(),
        new byte[] {(byte) 0x72, (byte) 0x5a, (byte) 0x4d, (byte) 0x53, (byte) 0x39, (byte) 0x5f, (byte) 0x56,
            (byte) 0x71, (byte) 0x58, (byte) 0x41, (byte) 0x7c, (byte) 0x6d, (byte) 0x31, (byte) 0x47, (byte) 0x51,
            (byte) 0x43, (byte) 0x3b, (byte) 0x3d, (byte) 0x62, (byte) 0x53});
    Assert.assertEquals(greeting.getServerCapabilities(),
        EnumSet.of(ClientCapability.LONG_COLUMN_FLAG, ClientCapability.CONNECT_WITH_DB, ClientCapability.COMPRESS,
            ClientCapability.PROTOCOL_4_1, ClientCapability.TRANSACTIONS, ClientCapability.SECURE_CONNECTION));
    Assert.assertEquals(greeting.getCharacterSet(), MysqlCharacterSet.LATIN1_SWEDISH_CI);
    Assert.assertEquals(greeting.getServerStatus(), EnumSet.of(ServerStatus.AUTOCOMMIT));
  }

  private HandshakeResponse castToServerGreeting(InputStream in, MySqlClientDecoder decoder) throws IOException {
    return (HandshakeResponse) decoder.decode(in, new EmbeddedChannel(), true);
  }

  private MySqlConnection createMockConnection() {
    Configuration configuration = new Configuration();
    configuration.setHost("localhost");
    configuration.setPassword("sa");
    configuration.setUsername("sa");
    configuration.setDatabase("test");
    configuration.setPort(42);
    return new MySqlConnection(login, 64, new MysqlConnectionManager(configuration, new HashMap<String, String>()),
        new NettyChannel(new EmbeddedChannel()), StackTracingOptions.GLOBAL_DEFAULT);
  }

}
