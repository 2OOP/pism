package org.toop;

import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.backend.ServerManager;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.frontend.ConnectionManager;
import org.toop.frontend.UI.LocalServerSelector;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static boolean running = false;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //        Logging.disableAllLogs();
        //		Logging.enableAllLogsForClass(LocalTicTacToe.class);
        //        Logging.enableLogsForClass(ServerManager.class, Level.ALL);
        //        Logging.enableLogsForClass(TicTacToeServer.class, Level.ALL);
        //        Logging.enableLogsForClass(TcpClient.class, Level.ALL);
        //        Logging.enableLogsForClass(ConnectionManager.class, Level.ALL);
        initSystems();
        registerEvents();

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
        new ConnectionManager();
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
