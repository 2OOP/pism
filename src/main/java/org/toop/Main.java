package org.toop;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.ServerConnection;
import org.toop.server.ServerManager;
import org.toop.server.backend.Testsss;
import org.toop.server.backend.TcpServer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        TcpServer server = new TcpServer(5001);
        Thread serverThread = new Thread(server);
        serverThread.start();

        ServerManager serverManager = new ServerManager();

        CompletableFuture<String> future = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.1", "5001", future));
        String serverId = future.get();
        logger.info("Server ID: " + serverId);

        CompletableFuture<String> future2 = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.2", "5002", future2));
        String serverId2 = future2.get();
        logger.info("Server ID: " + serverId2);

        CompletableFuture<String> future3 = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.3", "5003", future3));
        String serverId3 = future3.get();
        logger.info("Server ID: " + serverId3);

        CompletableFuture<String> future4 = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.4", "5004", future4));
        String serverId4 = future4.get();
        logger.info("Server ID: " + serverId4);

        CompletableFuture<String> future5 = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.5", "5005", future5));
        String serverId5 = future5.get();
        logger.info("Server ID: " + serverId5);

//        GlobalEventBus.post(new Events.ServerEvents.StartConnection("127.0.0.1", "5001"));


//        Server.startNew("127.0.0.1", "5001");
//        Testsss.start(""); // Used for testing server.
        Window.start("");

        CompletableFuture<String> future6 = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.RequestsAllConnections(future6));
        String serverConnections = future6.get();
        logger.info("Running connections: {}", serverConnections);

    }

}