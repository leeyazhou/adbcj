/**
 * 
 */
package org.adbcj.mysql;

import org.adbcj.mysql.codec.packets.response.AbstractResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author lee
 */
public class MySqlHandler {
  private static final Logger logger = LoggerFactory.getLogger(MySqlHandler.class);

  public void handleResponse(AbstractResponse response) {
    logger.info("处理消息: {}", response);

  }
}
