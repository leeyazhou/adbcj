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
/**
 * 
 */
package com.ly.train.flower.db.api;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author lee
 */
public class Configuration {

  private String url;
  private String host;
  private Integer port;
  private String username;
  private String password;
  private String database;
  private Charset charset;
  private Integer connectionTimeout = 5000;
  private Integer testTimeout = 5000;
  private Integer queryTimeout = 5000;
  private Map<String, String> properties = new HashMap<String, String>();

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }



  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the database
   */
  public String getDatabase() {
    return database;
  }

  /**
   * @param database the database to set
   */
  public void setDatabase(String database) {
    this.database = database;
  }

  /**
   * @return the charset
   */
  public Charset getCharset() {
    return charset;
  }

  /**
   * @param charset the charset to set
   */
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  /**
   * @return the connectionTimeout
   */
  public Integer getConnectionTimeout() {
    return connectionTimeout;
  }

  /**
   * @param connectionTimeout the connectionTimeout to set
   */
  public void setConnectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  /**
   * @return the testTimeout
   */
  public Integer getTestTimeout() {
    return testTimeout;
  }

  /**
   * @param testTimeout the testTimeout to set
   */
  public void setTestTimeout(Integer testTimeout) {
    this.testTimeout = testTimeout;
  }

  /**
   * @return the queryTimeout
   */
  public Integer getQueryTimeout() {
    return queryTimeout;
  }

  /**
   * @param queryTimeout the queryTimeout to set
   */
  public void setQueryTimeout(Integer queryTimeout) {
    this.queryTimeout = queryTimeout;
  }


  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @param host the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * @return the port
   */
  public Integer getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(Integer port) {
    this.port = port;
  }


  /**
   * @return the properties
   */
  public Map<String, String> getProperties() {
    return properties;
  }

  public boolean containsProperty(String key) {
    return this.properties.containsKey(key);
  }

  public void addProperty(String key, String value) {
    this.properties.put(key, value);
  }
}
