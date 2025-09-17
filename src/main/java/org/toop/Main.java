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

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {

        initSystems();

        CompletableFuture<String> serverIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartServerRequest("5001", "tictactoe", serverIdFuture));
        String serverId = serverIdFuture.get();

        CompletableFuture<String> connectionIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.1", "5001", connectionIdFuture));
        String connectionId = connectionIdFuture.get();

        CompletableFuture<String> ticTacToeGame = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.CreateTicTacToeGameRequest(serverId, "John", "Pim", ticTacToeGame));
        String ticTacToeGameId = ticTacToeGame.get();

        GlobalEventBus.post(new Events.ServerEvents.RunTicTacToeGame(serverId, ticTacToeGameId));

		ConsoleGui console = new ConsoleGui();
		GameBase.State state = GameBase.State.INVALID;

		console.print();

		while (console.next()) {
			console.print();
		}

		console.print();
    }

    public static void initSystems() {
        new ServerManager();
        new ConnectionManager();
    }
}
