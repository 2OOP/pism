package org.toop;

import org.toop.UI.LocalServerSelector;
import org.toop.eventbus.EventRegistry;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.backend.ServerManager;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.toop.server.frontend.ConnectionManager;

import java.util.concurrent.ExecutionException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static boolean running = false;

	public static void main(String[] args) throws ExecutionException, InterruptedException {
//        Logging.disableAllLogs();
		Logging.disableLogsForClass(EventRegistry.class);
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

    /**
     * Returns false if any event could not be initialized.
     */

	private static void registerEvents() {
		GlobalEventBus.subscribeAndRegister(Events.WindowEvents.OnQuitRequested.class, event -> {
			quit();
		});

		GlobalEventBus.subscribeAndRegister(Events.WindowEvents.OnMouseMove.class, event -> {
		});
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
