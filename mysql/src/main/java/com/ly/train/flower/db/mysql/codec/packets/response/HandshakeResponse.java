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
package com.ly.train.flower.db.mysql.codec.packets.response;

import java.util.Collections;
import java.util.Set;
import com.ly.train.flower.db.mysql.codec.model.ClientCapability;
import com.ly.train.flower.db.mysql.codec.model.MysqlCharacterSet;
import com.ly.train.flower.db.mysql.codec.model.ServerStatus;

public class HandshakeResponse extends AbstractResponse {
  private final int protocol;
  private final String version;
  private final int threadId;
  private final byte[] salt;
  private final Set<ClientCapability> serverCapabilities;
  private final MysqlCharacterSet characterSet;
  private final Set<ServerStatus> serverStatus;

  public HandshakeResponse(int length, int packetNumber, int protocol, String version, int threadId, byte[] salt,
      Set<ClientCapability> serverCapabilities, MysqlCharacterSet characterSet, Set<ServerStatus> serverStatus) {
    super(length, packetNumber);
    this.protocol = protocol;
    this.version = version;
    this.threadId = threadId;
    this.salt = salt;
    this.serverCapabilities = serverCapabilities;
    this.characterSet = characterSet;
    this.serverStatus = serverStatus;
  }

  public int getProtocol() {
    return protocol;
  }

  public String getVersion() {
    return version;
  }

  public int getThreadId() {
    return threadId;
  }

  public byte[] getSalt() {
    return salt;
  }

  public Set<ClientCapability> getServerCapabilities() {
    return Collections.unmodifiableSet(serverCapabilities);
  }

  public MysqlCharacterSet getCharacterSet() {
    return characterSet;
  }

  public Set<ServerStatus> getServerStatus() {
    return serverStatus;
  }

}
