package org.toop.server.frontend;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private static final Logger logger = LogManager.getLogger(ConnectionManager.class);

    /**
     * Map of serverId -> Server instances
     */
    private final Map<String, ServerConnection> serverConnections = new ConcurrentHashMap<>();

    /**
     * Starts a connection manager, to manage, connections.
     */
    public ConnectionManager() {
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.StartConnectionRequest.class, this::handleStartConnectionRequest);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.StartConnection.class, this::handleStartConnection);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.SendCommand.class, this::handleCommand);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.Reconnect.class, this::handleReconnect);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.ChangeConnection.class, this::handleChangeConnection);
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.ForceCloseAllConnections.class, _ -> shutdownAll());
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.RequestsAllConnections.class, this::getAllConnections);
    }

    private String startConnectionRequest(String ip, String port) {
        String connectionId = UUID.randomUUID().toString();
        ServerConnection connection = new ServerConnection(connectionId, ip, port);
        this.serverConnections.put(connectionId, connection);
        new Thread(connection, "Connection-" + connectionId).start();
        logger.info("Connected to server {} at {}:{}", connectionId, ip, port);
        return connectionId;
    }

    private void handleStartConnectionRequest(Events.ServerEvents.StartConnectionRequest request) {
        request.future().complete(this.startConnectionRequest(request.ip(), request.port())); // TODO: Maybe post ConnectionEstablished event.
    }

    private void handleStartConnection(Events.ServerEvents.StartConnection event) {
        GlobalEventBus.post(new Events.ServerEvents.ConnectionEstablished(
                this.startConnectionRequest(event.ip(), event.port()),
                event.ip(),
                event.port()
        ));
    }

    private void handleCommand(Events.ServerEvents.SendCommand event) { // TODO: Move this to ServerConnection class, keep it internal.
        ServerConnection serverConnection = this.serverConnections.get(event.connectionId());
        if (serverConnection != null) {
            serverConnection.sendCommandByString(event.args());
        } else {
            logger.warn("Server {} not found for command '{}'", event.connectionId(), event.args());
        }
    }

    private void handleReconnect(Events.ServerEvents.Reconnect event) {
        ServerConnection serverConnection = this.serverConnections.get(event.connectionId());
        if (serverConnection != null) {
            try {
                serverConnection.reconnect();
                logger.info("Server {} reconnected", event.connectionId());
            } catch (Exception e) {
                logger.error("Server {} failed to reconnect", event.connectionId(), e);
                GlobalEventBus.post(new Events.ServerEvents.CouldNotConnect(event.connectionId()));
            }
        }
    }

    private void handleChangeConnection(Events.ServerEvents.ChangeConnection event) {
        ServerConnection serverConnection = this.serverConnections.get(event.connectionId());
        if (serverConnection != null) {
            try {
                serverConnection.connect(event.ip(), event.port());
                logger.info("Server {} changed connection to {}:{}", event.connectionId(), event.ip(), event.port());
            } catch (Exception e) {
                logger.error("Server {} failed to change connection", event.connectionId(), e);
                GlobalEventBus.post(new Events.ServerEvents.CouldNotConnect(event.connectionId()));
            }
        }
    }

    private void getAllConnections(Events.ServerEvents.RequestsAllConnections request) {
        List<ServerConnection> a = new ArrayList<>(this.serverConnections.values());
        request.future().complete(a.toString());
    }

    public void shutdownAll() {
        this.serverConnections.values().forEach(ServerConnection::closeConnection);
        this.serverConnections.clear();
        logger.info("All servers shut down");
    }
}