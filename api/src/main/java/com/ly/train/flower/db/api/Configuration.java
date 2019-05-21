/**
 * 
 */
package com.ly.train.flower.db.api;

import java.nio.charset.Charset;

/**
 * 
 * @author lee
 */
public class Configuration {

  private String username;
  private String host;
  private Integer port;
  private String password;
  private String database;
  private Charset charset;
  private Integer connectionTimeout = 5000;
  private Integer testTimeout = 5000;
  private Integer queryTimeout = 5000;

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


}
