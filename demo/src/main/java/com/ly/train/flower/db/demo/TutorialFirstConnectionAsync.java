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

public class TutorialFirstConnectionAsync {

    public static void main(String[] args) {
        // First, let's start a demo H2 database server
        Server demoH2Db = DemoServer.startServer();

        final ConnectionManager connectionManager = ConnectionManagerProvider.createConnectionManager(
                "asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE",
                "asyncdb",
                "password1234"
        );

        // Aysync with raw callbacks.
        connectionManager.connect((connection, connectionFailure) -> {
            if (connectionFailure == null) {
                System.out.println("Connected!");
                // No failure, continue the operations
                connection.close((connectionClosed, closeFailure) -> {
                    if (closeFailure != null) {
                        closeFailure.printStackTrace();
                    } else {
                        System.out.println("Closed!");
                    }

                    // At the end, close the connection manger
                    connectionManager.close((managerClosed, managerCloseFailure) -> {
                        if (managerCloseFailure != null) {
                            managerCloseFailure.printStackTrace();
                        }
                        System.exit(0);
                    });
                });
            } else {
                // Otherwise, back out and print the error
                connectionFailure.printStackTrace();
                System.exit(-1);
            }
        });


        demoH2Db.shutdown();
    }
}
