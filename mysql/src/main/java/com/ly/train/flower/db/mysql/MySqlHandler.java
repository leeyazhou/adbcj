/**
 * 
 */
package com.ly.train.flower.db.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.db.mysql.codec.packets.response.AbstractResponse;

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
