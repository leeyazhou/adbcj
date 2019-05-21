package com.ly.train.flower.db.tck.test;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.ly.train.flower.db.api.Connection;

public class ConnectDiffrentUsersTest extends AbstractWithConnectionManagerTest {

  @Test
  public void connectWithOtherUser() throws Exception {

    Connection normalUser = connectionManager.connect().get();
    Connection connectionOtherUser =
        connectionManager.connect("asyncdb-other-user".toUpperCase(), "asyncdb-other-user").get();

    String userNormal = normalUser.executeQuery("SELECT current_user()").get().get(0).get(0).getString();
    String otherUser = connectionOtherUser.executeQuery("SELECT current_user()").get().get(0).get(0).getString();

    Assert.assertTrue(userNormal.toLowerCase().contains("asyncdbtck"));
    Assert.assertTrue(otherUser.toLowerCase().contains("asyncdb-other-user"));

    normalUser.close();
    connectionOtherUser.close();
  }
}
