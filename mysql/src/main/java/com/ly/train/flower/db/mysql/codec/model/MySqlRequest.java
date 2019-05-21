package com.ly.train.flower.db.mysql.codec.model;

import com.ly.train.flower.db.api.DbCallback;
import com.ly.train.flower.db.mysql.codec.decoder.AbstractDecoder;
import com.ly.train.flower.db.mysql.codec.packets.request.AbstractRequest;


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
