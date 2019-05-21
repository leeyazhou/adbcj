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
import com.ly.train.flower.db.api.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TutorialQueueLimit {

    public static void main(String[] args) throws Exception {
        // First, let's start a demo H2 database server
        Server demoH2Db = DemoServer.startServer();

        firstRunToSmallQueue();
        Thread.sleep(1000);
        System.out.println("========================================");
        System.out.println("Second try, with longer request queue");
        System.out.println("========================================");
        Thread.sleep(100);
        secondRunWithLongerQueue();

        demoH2Db.shutdown();
    }

    private static void firstRunToSmallQueue() throws Exception {
        final ConnectionManager connectionManager = ConnectionManagerProvider.createConnectionManager(
                "asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE",
                "asyncdb",
                "password1234"
        );


        int amountOfQueries = 200;
        runQueries(connectionManager, amountOfQueries);

        connectionManager.close().get();
    }

    private static void secondRunWithLongerQueue() throws Exception {
        int maxQueueLength = 200;
        Map<String, String> props = new HashMap<>();
        props.put(StandardProperties.MAX_QUEUE_LENGTH, String.valueOf(maxQueueLength));
        final ConnectionManager connectionManager = ConnectionManagerProvider.createConnectionManager(
                "asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE",
                "asyncdb",
                "password1234",
                props
        );

        runQueries(connectionManager, maxQueueLength);

        connectionManager.close().get();
    }

    private static void runQueries(ConnectionManager connectionManager, int amountOfQueries) throws Exception {
        AtomicInteger failures = new AtomicInteger();
        CountDownLatch waitForCompletion = new CountDownLatch(amountOfQueries);

        // Blocking code to keep demo code short
        Connection connection = connectionManager.connect().get();
        PreparedQuery stmt = connection.prepareQuery("SELECT ?").get();

        // Issue 200 requests, without waiting for any response
        for (int i = 0; i < 200; i++) {
            stmt.execute(i).handle((rows, queryFailure) -> {
                if (queryFailure == null) {
                    System.out.println(" - Yeah, Result: " + rows.get(0).get(0));
                } else {
                    failures.incrementAndGet();
                    System.err.println(" - Oups, we send to many queries");
                    queryFailure.printStackTrace(System.err);
                }
                waitForCompletion.countDown();
                return null;
            });
        }

        waitForCompletion.await();
        System.out.println("================================");
        System.out.println("Failed request: "+failures.get());

        connection.close();
    }


}
