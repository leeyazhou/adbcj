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
package com.ly.train.flower.db.demo;

import org.h2.tools.Server;
import com.ly.train.flower.db.api.Connection;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;
import com.ly.train.flower.db.api.StandardProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorialConnectionPool {


    private static final int TEST_CONNECTION_COUNT = 100;

    public static void main(String[] args) throws Exception {
        // First, let's start a demo H2 database server
        Server demoH2Db = DemoServer.startServer();

        Map<String, String> settings = new HashMap<>();
        settings.put(StandardProperties.CONNECTION_POOL_ENABLE, "true");
        final ConnectionManager connectionManager = ConnectionManagerProvider.createConnectionManager(
                "asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE",
                "asyncdb",
                "password1234",
                settings
        );

        long firstTime = System.currentTimeMillis();
        openCloseBunchOfConnections(connectionManager);
        System.out.println("First time: Time to connect: " + ((System.currentTimeMillis()-firstTime)/ TEST_CONNECTION_COUNT )+"ms");


        long secondTime = System.currentTimeMillis();
        openCloseBunchOfConnections(connectionManager);
        System.out.println("First time, pooled: Time to connect: " + ((System.currentTimeMillis()-secondTime)/ TEST_CONNECTION_COUNT )+"ms");

        connectionManager.close().get();

        demoH2Db.shutdown();
    }

    private static void openCloseBunchOfConnections(ConnectionManager connectionManager) throws Exception {
        ArrayList<Connection> connections = new ArrayList<>();
        for (int i = 0; i < TEST_CONNECTION_COUNT; i++) {
            connections.add(connectionManager.connect().get());
        }
        for (Connection connection : connections) {
            connection.close().get();
        }
    }
}
