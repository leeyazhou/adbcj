/*
	This file is part of asyncdb.

	asyncdb is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	asyncdb is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with asyncdb.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2008  Mike Heath
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
