package org.toop;

import org.toop.server.backend.ServerManager;
import org.toop.server.frontend.ConnectionManager;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.ExecutionException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        initSystems();
        Logging.disableLogs();

		ConsoleGui console = new ConsoleGui();

		do {
			console.print();
		} while (console.next());

		console.print();
    }

    public static void initSystems() {
        new ServerManager();
        new ConnectionManager();
    }

}
