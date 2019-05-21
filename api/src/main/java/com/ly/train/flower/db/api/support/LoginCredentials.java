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

import com.ly.train.flower.db.api.Configuration;

public final class LoginCredentials {

  private final String userName;
  private final String password;
  private final String database;

  public LoginCredentials(Configuration configuration) {
    this.userName = configuration.getUsername();
    this.password = configuration.getPassword();
    this.database = configuration.getDatabase();
  }

  public LoginCredentials(String userName, String password, String database) {
    this.userName = userName;
    this.password = password;
    this.database = database;
  }



  public String getDatabase() {
    return database;
  }

  public String getPassword() {
    return password;
  }

  public String getUserName() {
    return userName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    LoginCredentials that = (LoginCredentials) o;

    if (!database.equals(that.database))
      return false;
    if (password != null ? !password.equals(that.password) : that.password != null)
      return false;
    return userName.equals(that.userName);
  }

  @Override
  public int hashCode() {
    int result = userName.hashCode();
    result = 31 * result + (password != null ? password.hashCode() : 0);
    result = 31 * result + database.hashCode();
    return result;
  }

  @Override
  public String toString() {
    // Mask password for security issue sine 2017-10-15 little-pan
    return "LoginCredentials{" + "userName='" + userName + '\'' + ", password='" + "******" + '\'' + ", database='"
        + database + '\'' + '}';
  }
}
