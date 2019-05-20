package org.adbcj.mysql.codec;

import org.adbcj.DbCallback;
import org.adbcj.mysql.codec.decoding.DecoderState;


public class MySqlRequest<T> {
  private final String description;
  private final DecoderState startState;
  private final ClientRequest request;
  private final DbCallback<T> callback;

  MySqlRequest(String description, DecoderState startState, ClientRequest request, DbCallback<T> callback) {
    this.description = description;
    this.startState = startState;
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
  public DecoderState getStartState() {
    return startState;
  }

  /**
   * @return the request
   */
  public ClientRequest getRequest() {
    return request;
  }

  /**
   * @return the callback
   */
  public DbCallback<T> getCallback() {
    return callback;
  }
}
