package org.adbcj.mysql.codec.model;

import org.adbcj.DbCallback;
import org.adbcj.mysql.codec.decoder.AbstractDecoder;
import org.adbcj.mysql.codec.packets.request.AbstractRequest;


public class MySqlRequest<T> {
  private final String description;
  private final AbstractDecoder decoder;
  private final AbstractRequest request;
  private final DbCallback<T> callback;

  public MySqlRequest(String description, AbstractDecoder decoder, AbstractRequest request, DbCallback<T> callback) {
    this.description = description;
    this.decoder = decoder;
    this.request = request;
    this.callback = callback;
  }

  @Override
  public String toString() {
    return "MySqlRequest{" + description + '}';
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the startState
   */
  public AbstractDecoder getDecoder() {
    return decoder;
  }

  /**
   * @return the request
   */
  public AbstractRequest getRequest() {
    return request;
  }

  /**
   * @return the callback
   */
  public DbCallback<T> getCallback() {
    return callback;
  }
}
