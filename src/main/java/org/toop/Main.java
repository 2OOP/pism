package org.toop;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.backend.ServerManager;
import org.toop.server.frontend.ConnectionManager;
import org.toop.server.backend.TcpServer;

import org.toop.game.*;
import org.toop.game.tictactoe.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        initSystems();

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
