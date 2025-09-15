package org.toop;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.backend.ServerManager;
import org.toop.server.frontend.ConnectionManager;
import org.toop.server.backend.TcpServer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {

//        TcpServer server = new TcpServer(5001);
//        Thread serverThread = new Thread(server);
//        serverThread.start();

        initSystems();

        GlobalEventBus.post(new Events.ServerEvents.StartServer("5001"));

        CompletableFuture<String> future = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.1", "5001", future));
        String serverId = future.get();

//        for (int i = 0; i < 1; i++) {
//            Thread thread = new Thread(() -> {
////                logger.info("Server ID: {}", serverId);
//                GlobalEventBus.post(new Events.ServerEvents.Command(serverId, "HELP", "TEST"));
//            });
//            thread.start();
//        }

        GlobalEventBus.post(new Events.ServerEvents.Command(serverId, "HELP", "TEST"));

        GlobalEventBus.post(new Events.ServerEvents.ForceCloseAllConnections());
        GlobalEventBus.post(new Events.ServerEvents.ForceCloseAllServers());

//
//        CompletableFuture<String> future2 = new CompletableFuture<>();
//        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.1", "5001", future2));
//        String serverId2 = future.get();
//        logger.info("Server ID: {}", serverId2);
//        GlobalEventBus.post(new Events.ServerEvents.Command(serverId2, "HELP", "TEST2"));

//        GlobalEventBus.post(new Events.ServerEvents.StartConnection("127.0.0.1", "5001"));


//        Server.startNew("127.0.0.1", "5001");
//        Testsss.start(""); // Used for testing server.
//        Window.start("");

//        CompletableFuture<String> future6 = new CompletableFuture<>();
//        GlobalEventBus.post(new Events.ServerEvents.RequestsAllConnections(future6));
//        String serverConnections = future6.get();
//        logger.info("Running connections: {}", serverConnections);

    }

    public static void initSystems() {
        new ServerManager();
        new ConnectionManager();
    }

}