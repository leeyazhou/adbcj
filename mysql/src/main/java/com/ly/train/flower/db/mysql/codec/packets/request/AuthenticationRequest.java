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
package com.ly.train.flower.db.mysql.codec.packets.request;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import com.ly.train.flower.db.api.support.LoginCredentials;
import com.ly.train.flower.db.mysql.codec.model.ClientCapability;
import com.ly.train.flower.db.mysql.codec.model.ClientCapabilityExtend;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.util.IOUtil;
import com.ly.train.flower.db.mysql.codec.util.PasswordEncryption;


/**
 * 认证信息
 * 
 * @author lee
 */
public class AuthenticationRequest extends AbstractRequest {

  public static final int MAX_PACKET_SIZE = 0x00ffffff;

  public static final int FILLER_LENGTH = 23;
  public static final int PASSWORD_LENGTH = 20;

  private final LoginCredentials credentials;
  private final Set<ClientCapability> capabilities;
  private final Set<ClientCapabilityExtend> extendedCapabilities;
  private final MysqlCharacterSet charset;

  private final byte[] salt;

  public AuthenticationRequest(LoginCredentials credentials, Set<ClientCapability> capabilities,
      Set<ClientCapabilityExtend> extendedCapabilities, MysqlCharacterSet charset, byte[] salt) {
    super();
    this.credentials = credentials;
    this.capabilities = capabilities;
    this.extendedCapabilities = extendedCapabilities;
    this.charset = charset;
    this.salt = salt.clone();
  }

  @Override
  public int getLength() throws UnsupportedEncodingException {
    return 2 // Client Capabilities field
        + 2 // Extended Client Capabilities field
        + 4 // Max packet size field
        + 1 // Char set
        + FILLER_LENGTH + credentials.getUserName().getBytes(MysqlCharacterSet.UTF8_UNICODE_CI.getCharsetName()).length
        + 1 + ((credentials.getPassword() == null || credentials.getPassword().length() == 0) ? 0 : PASSWORD_LENGTH) + 1 // Filler
                                                                                                                         // after
                                                                                                                         // password
        + credentials.getDatabase().getBytes(MysqlCharacterSet.UTF8_UNICODE_CI.getCharsetName()).length + 1;
  }

  @Override
  public boolean hasPayload() {
    return true;
  }

  @Override
  public void writeToOutputStream(OutputStream out) throws IOException {
    // Encode initial part of authentication request
    IOUtil.writeEnumSetShort(out, getCapabilities());
    IOUtil.writeEnumSetShort(out, getExtendedCapabilities());
    IOUtil.writeInt(out, getMaxPacketSize());
    out.write(getCharSet().getId());
    out.write(new byte[AuthenticationRequest.FILLER_LENGTH]);

    out.write(getCredentials().getUserName().getBytes(MysqlCharacterSet.UTF8_UNICODE_CI.getCharsetName()));
    out.write(0); // null-terminate username

    // Encode password
    final String password = getCredentials().getPassword();
    if (password != null && password.length() > 0) {
      byte[] salt = getSalt();
      byte[] encryptedPassword = PasswordEncryption.encryptPassword(password, salt);
      out.write(encryptedPassword.length);
      out.write(encryptedPassword);
    } else {
      out.write(0); // null-terminate password
    }

    // Encode desired database/schema
    final String database = getCredentials().getDatabase();
    if (database != null) {
      out.write(database.getBytes(MysqlCharacterSet.UTF8_UNICODE_CI.getCharsetName()));
    }
    out.write(0);
  }

  @Override
  public int getPacketNumber() {
    return 1;
  }

  @Override
  public String toString() {
    return "LoginRequest{" + "credentials=" + credentials + '}';
  }

  public Set<ClientCapability> getCapabilities() {
    return capabilities;
  }

  public Set<ClientCapabilityExtend> getExtendedCapabilities() {
    return extendedCapabilities;
  }

  public LoginCredentials getCredentials() {
    return credentials;
  }

  public int getMaxPacketSize() {
    return MAX_PACKET_SIZE; // TODO Make MySQL max packet size configurable
  }

  public MysqlCharacterSet getCharSet() {
    return charset;
  }

  public byte[] getSalt() {
    return salt.clone();
  }

}
