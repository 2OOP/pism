package org.toop.server.backend;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.server.backend.tictactoe.TicTacToeServer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// TODO more methods.

public class ServerManager {

    private static final Logger logger = LogManager.getLogger(ServerManager.class);

    /**
     * Map of serverId -> Server instances
     */
    private final Map<String, TcpServer> servers = new ConcurrentHashMap<>();

    /**
     * Starts a server manager, to manage, servers.
     */
    public ServerManager() {
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.StartServerRequest.class, this::handleStartServerRequest);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.StartServer.class, this::handleStartServer);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.ForceCloseAllServers.class, _ -> shutdownAll());
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.CreateTicTacToeGame.class, this::handleStartTicTacToeGameOnAServer);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.RunTicTacToeGame.class, this::handleRunTicTacToeGameOnAServer);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.EndTicTacToeGame.class, this::handleEndTicTacToeGameOnAServer);
    }

    private String startServer(String port, String gameType) {
        String serverId = UUID.randomUUID().toString();
        gameType = gameType.toLowerCase();
        try {
            TcpServer server = null;
            if (Objects.equals(gameType, "tictactoe")) {
                server = new TicTacToeServer(Integer.parseInt(port));
            }
            else {
                logger.error("Manager could not create a TcpServer for {}", gameType);
                return null;
            }
            this.servers.put(serverId, server);
            new Thread(server, "Server-" + serverId).start();
            logger.info("Connected to server {} at {}", serverId, port);
            return serverId;
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            return null;
        }
    }

    private void handleStartServerRequest(Events.ServerEvents.StartServerRequest request) {
        request.future().complete(this.startServer(request.port(), request.gameType())); // TODO: Maybe post StartServer event.
    }

    private void handleStartServer(Events.ServerEvents.StartServer event) {
        GlobalEventBus.post(new Events.ServerEvents.ServerStarted(
                this.startServer(event.port(), event.gameType()),
                event.port()
        ));
    }

    private void handleStartTicTacToeGameOnAServer(Events.ServerEvents.CreateTicTacToeGame event) {
        TicTacToeServer serverThing = (TicTacToeServer) this.servers.get(event.serverUuid());
        String gameId = null;
        if (serverThing != null) {
            try {
                gameId = serverThing.newGame(event.playerA(), event.playerB());
                logger.info("Created game on server {}", event.serverUuid());
            }
            catch (Exception e) { // TODO: Error handling
                logger.info("Could not create game on server {}", event.serverUuid());
            }
        }
        event.future().complete(gameId);
    }

    private void handleRunTicTacToeGameOnAServer(Events.ServerEvents.RunTicTacToeGame event) {
        TicTacToeServer gameServer = (TicTacToeServer) this.servers.get(event.serverUuid());
        gameServer.runGame(event.gameUuid());
    }

    private void handleEndTicTacToeGameOnAServer(Events.ServerEvents.EndTicTacToeGame event) {
        TicTacToeServer gameServer = (TicTacToeServer) this.servers.get(event.serverUuid());
        gameServer.endGame(event.gameUuid());
    }

    private void getAllServers(Events.ServerEvents.RequestsAllServers request) {
        ArrayList<TcpServer> a = new ArrayList<>(this.servers.values());
        request.future().complete(a.toString());
    }

    public void shutdownAll() {
        this.servers.values().forEach(TcpServer::stop);
        this.servers.clear();
        logger.info("All servers shut down");
    }
}
