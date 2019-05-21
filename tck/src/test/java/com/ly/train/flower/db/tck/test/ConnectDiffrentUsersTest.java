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
