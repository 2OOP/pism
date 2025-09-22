package org.toop;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Supplier;
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
import org.toop.frontend.networking.NetworkingGameClientHandler;

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

//        CompletableFuture<String> conIdFuture = new CompletableFuture<>();
//        GlobalEventBus.post(
//                new NetworkEvents.StartClientRequest(NetworkingGameClientHandler::new,
//                        "127.0.0.1", 5001, conIdFuture));
//        var conId = conIdFuture.get();

        int numThreads = 100; // how many EventPublisher tests you want

        ExecutorService executor = Executors.newFixedThreadPool(200); // 20 threads in pool

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                new EventPublisher<>(
                        NetworkEvents.StartClient.class,
                        (Supplier<NetworkingGameClientHandler>) NetworkingGameClientHandler::new,
                        "127.0.0.1",
                        5001
                ).onEventById(
                        NetworkEvents.StartClientSuccess.class,
                        event -> GlobalEventBus.post(
                                new NetworkEvents.CloseClient((String) event.connectionId()))
                ).unregisterAfterSuccess()
                .postEvent();
            });
        }

// Shutdown after tasks complete
        executor.shutdown();

//        GlobalEventBus.post(new NetworkEvents.SendCommand(conId, "move", "5"));
//        GlobalEventBus.post(new NetworkEvents.ForceCloseAllClients());
//        GlobalEventBus.post(new NetworkEvents.StartClient(
//                NetworkingGameClientHandler::new, "127.0.0.1", 5001, serverId
//        ));

        //		JFrame frame = new JFrame("Server Settings");
        //		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //		frame.setSize(800, 600);
        //		frame.setLocationRelativeTo(null);
        //		frame.setVisible(true);

        javax.swing.SwingUtilities.invokeLater(LocalServerSelector::new);

        //		new Thread(() -> {
        //			LocalServerSelector window = new LocalServerSelector();
        //		}).start();

    }

    private static void registerEvents() {
        GlobalEventBus.subscribeAndRegister(
                Events.WindowEvents.OnQuitRequested.class,
                event -> {
                    quit();
                });

        GlobalEventBus.subscribeAndRegister(Events.WindowEvents.OnMouseMove.class, event -> {});
    }

    public static void initSystems() {
        new ServerManager();
        new NetworkingClientManager();
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
