package com.ly.train.flower.db.demo;

import org.h2.tools.Server;
import com.ly.train.flower.db.api.ConnectionManager;
import com.ly.train.flower.db.api.ConnectionManagerProvider;

public class TutorialFirstConnectionFuture {

    public static void main(String[] args) {
        // First, let's start a demo H2 database server
        Server demoH2Db = DemoServer.startServer();

        final ConnectionManager connectionManager = ConnectionManagerProvider.createConnectionManager(
                "asyncdb:h2://localhost:14242/mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE",
                "asyncdb",
                "password1234"
        );

        connectionManager.connect().thenCompose(connection -> {
            System.out.println("Connected!");
            return connection.close();
        }).thenCompose(closeComplete -> {
            System.out.println("Close complete!");
            return connectionManager.close();
        }).whenComplete((complete, error) -> {
            if (error != null) {
                error.printStackTrace();
            }
            System.exit(-1);
        });

    }
}
