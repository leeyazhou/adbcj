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
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;

public class TutorialDealingWithErrors {

    public static void main(String[] args) throws Exception {
        // First, let's start a demo H2 database server
        Server demoH2Db = DemoServer.startServer();

        final ConnectionManager connectionManager = ConnectionManagerProvider.createConnectionManager(
                "asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE",
                "asyncdb",
                "password1234"
        );

        oupsError(connectionManager);

    }

    /**
     * If you just run this, the error stack trace will not reference this method at all.
     * <p>
     * When you use '-Dorg.asyncdb.debug=true' as JVM start up parameter, this method will show up in the cause.
     * Alternative: Add property "org.asyncdb.debug"="true" in the properties hashmap when connecting
     * <p>
     * Note: This feature has a high overhead. It is indent for to be used during development and debugging times
     *
     */
    private static void oupsError(ConnectionManager connectionManager) {
        connectionManager.connect().thenAccept(connection -> connection.executeQuery(
                "SELECT * FROM not-existing-table")
                .thenAccept(queryResult -> {
                    // No result, since wrong SQL.
                }).whenComplete((res, failure) -> {
            if (failure != null) {
                failure.printStackTrace();
            }
            connection.close();
            connectionManager.close();
        }));
    }
}
