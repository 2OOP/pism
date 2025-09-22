package org.toop;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.backend.ServerManager;
import org.toop.eventbus.EventPublisher;
import org.toop.eventbus.EventRegistry;
import org.toop.eventbus.events.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.eventbus.events.NetworkEvents;
import org.toop.frontend.UI.LocalServerSelector;
import org.toop.frontend.networking.NetworkingClientManager;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static boolean running = false;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //        Logging.disableAllLogs();
        		Logging.enableAllLogsForClass(EventRegistry.class);
        //        Logging.enableLogsForClass(ServerManager.class, Level.ALL);
        //        Logging.enableLogsForClass(TicTacToeServer.class, Level.ALL);
        //        Logging.enableLogsForClass(TcpClient.class, Level.ALL);
        //        Logging.enableLogsForClass(NetworkingClientManager.class, Level.ALL);

        initSystems();
        registerEvents();

        CompletableFuture<String> serverIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(
                new Events.ServerEvents.StartServerRequest(5001, "tictactoe", serverIdFuture));
        var serverId = serverIdFuture.get();

        var a = new MainTest();

        javax.swing.SwingUtilities.invokeLater(LocalServerSelector::new);

    }

    private static void initSystems() {
        new ServerManager();
        new NetworkingClientManager();
    }

    private static void registerEvents() {
        new EventPublisher().onEvent(Events.WindowEvents.OnQuitRequested.class).perform(_ -> quit());
        new EventPublisher().onEvent(Events.WindowEvents.OnMouseMove.class).perform(_ -> {});
    }

    private static void quit() {
        running = false;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Main.running = running;
    }
}
