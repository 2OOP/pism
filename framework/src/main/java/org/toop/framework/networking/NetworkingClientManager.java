package org.toop.framework.networking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;

public class NetworkingClientManager {

    private static final Logger logger = LogManager.getLogger(NetworkingClientManager.class);

    /** Map of serverId -> Server instances */
    final Map<Long, NetworkingClient> networkClients = new ConcurrentHashMap<>();

    /** Starts a connection manager, to manage, connections. */
    public NetworkingClientManager() throws NetworkingInitializationException {
        try {
            new EventFlow()
                    .listen(this::handleStartClient)
                    .listen(this::handleCommand)
                    .listen(this::handleSendLogin)
                    .listen(this::handleSendLogout)
                    .listen(this::handleSendGetPlayerlist)
                    .listen(this::handleSendGetGamelist)
                    .listen(this::handleSendSubscribe)
                    .listen(this::handleSendMove)
                    .listen(this::handleSendChallenge)
                    .listen(this::handleSendAcceptChallenge)
                    .listen(this::handleSendForfeit)
                    .listen(this::handleSendMessage)
                    .listen(this::handleSendHelp)
                    .listen(this::handleSendHelpForCommand)
                    .listen(this::handleCloseClient)
                    .listen(this::handleChangeClientHost)
                    .listen(this::handleGetAllConnections)
                    .listen(this::handleShutdownAll);
            logger.info("NetworkingClientManager initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize the client manager", e);
            throw e;
        }
    }

    long startClientRequest(String ip, int port) {
        long connectionId = SnowflakeGenerator.nextId();
        try {
            NetworkingClient client =
                    new NetworkingClient(
                            () -> new NetworkingGameClientHandler(connectionId),
                            ip,
                            port,
                            connectionId);
            client.setConnectionId(connectionId);
            this.networkClients.put(connectionId, client);
            logger.info("New client started successfully for {}:{}", ip, port);
        } catch (Exception e) {
            logger.error(e);
        }
        return connectionId;
    }

    private long startClientRequest(String ip, int port, long clientId) {
        try { //        With EventFlow
            NetworkingClient client =
                    new NetworkingClient(
                            () -> new NetworkingGameClientHandler(clientId), ip, port, clientId);
            client.setConnectionId(clientId);
            this.networkClients.replace(clientId, client);
            logger.info(
                    "New client started successfully for {}:{}, replaced: {}", ip, port, clientId);
        } catch (Exception e) {
            logger.error(e);
        }
        logger.info("Client {} started", clientId);
        return clientId;
    }

    void handleStartClient(NetworkEvents.StartClient event) {
        long id = this.startClientRequest(event.ip(), event.port());
        new Thread(
                        () ->
                                new EventFlow()
                                        .addPostEvent(
                                                NetworkEvents.StartClientResponse.class,
                                                id,
                                                event.eventSnowflake())
                                        .asyncPostEvent())
                .start();
    }

    void handleCommand(
            NetworkEvents.SendCommand
                    event) { // TODO: Move this to ServerConnection class, keep it internal.
        NetworkingClient client = this.networkClients.get(event.clientId());
        String args = String.join(" ", event.args());
        sendCommand(client, args);
    }

    void handleSendLogin(NetworkEvents.SendLogin event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, String.format("LOGIN %s", event.username()));
    }

    private void handleSendLogout(NetworkEvents.SendLogout event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, "LOGOUT");
    }

    private void handleSendGetPlayerlist(NetworkEvents.SendGetPlayerlist event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, "GET PLAYERLIST");
    }

    private void handleSendGetGamelist(NetworkEvents.SendGetGamelist event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, "GET GAMELIST");
    }

    private void handleSendSubscribe(NetworkEvents.SendSubscribe event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, String.format("SUBSCRIBE %s", event.gameType()));
    }

    private void handleSendMove(NetworkEvents.SendMove event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, String.format("MOVE %d", event.moveNumber()));
    }

    private void handleSendChallenge(NetworkEvents.SendChallenge event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(
                client,
                String.format("CHALLENGE %s %s", event.usernameToChallenge(), event.gameType()));
    }

    private void handleSendAcceptChallenge(NetworkEvents.SendAcceptChallenge event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, String.format("CHALLENGE ACCEPT %d", event.challengeId()));
    }

    private void handleSendForfeit(NetworkEvents.SendForfeit event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, "FORFEIT");
    }

    private void handleSendMessage(NetworkEvents.SendMessage event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, String.format("MESSAGE %s", event.message()));
    }

    private void handleSendHelp(NetworkEvents.SendHelp event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, "HELP");
    }

    private void handleSendHelpForCommand(NetworkEvents.SendHelpForCommand event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        sendCommand(client, String.format("HELP %s", event.command()));
    }

    private void sendCommand(NetworkingClient client, String command) {
        logger.info(
                "Preparing to send command: {} to server: {}:{}. clientId: {}",
                command.trim(),
                client.getHost(),
                client.getPort(),
                client.getId());
        client.writeAndFlushnl(command);
    }

    private void handleChangeClientHost(NetworkEvents.ChangeClientHost event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        client.closeConnection();
        startClientRequest(event.ip(), event.port(), event.clientId());
    }

    void handleCloseClient(NetworkEvents.CloseClient event) {
        NetworkingClient client = this.networkClients.get(event.clientId());
        client.closeConnection();
        this.networkClients.remove(event.clientId());
        logger.info("Client {} closed successfully.", event.clientId());
    }

    void handleGetAllConnections(NetworkEvents.RequestsAllClients request) {
        List<NetworkingClient> a = new ArrayList<>(this.networkClients.values());
        request.future().complete(a);
    }

    public void handleShutdownAll(NetworkEvents.ForceCloseAllClients request) {
        this.networkClients.values().forEach(NetworkingClient::closeConnection);
        this.networkClients.clear();
        logger.info("All servers shut down");
    }
}
