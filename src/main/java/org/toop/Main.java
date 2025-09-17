package org.toop;

import org.toop.UI.GameSelectorWindow;
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
        initSystems();
        registerEvents();
        /*
		Window window = Window.setup(Window.API.GLFW, "Test", new Window.Size(1280, 720));
		Renderer renderer = Renderer.setup(Renderer.API.OPENGL);

        if (!initEvents()) {
            throw new RuntimeException("A event could not be initialized");
        }

        TcpServer server = new TcpServer(5001);
        Thread serverThread = new Thread(server);
        serverThread.start();
        Server.start("127.0.0.1", "5001");
        // Testsss.start(""); // Used for testing server.
        Window.start("");
         */

        GameSelectorWindow gameSelectorWindow = new GameSelectorWindow();
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
