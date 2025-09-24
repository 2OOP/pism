package org.toop.framework.networking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventPublisher;
import org.toop.framework.eventbus.events.NetworkEvents;

public class NetworkingClientManager {

    private static final Logger logger = LogManager.getLogger(NetworkingClientManager.class);

    /** Map of serverId -> Server instances */
    private final Map<String, NetworkingClient> networkClients = new ConcurrentHashMap<>();

    /** Starts a connection manager, to manage, connections. */
    public NetworkingClientManager() {
        new EventPublisher<>(NetworkEvents.StartClientRequest.class, this::handleStartClientRequest);
        new EventPublisher<>(NetworkEvents.StartClient.class, this::handleStartClient);
        new EventPublisher<>(NetworkEvents.SendCommand.class, this::handleCommand);
        new EventPublisher<>(NetworkEvents.CloseClient.class, this::handleCloseClient);
        new EventPublisher<>(NetworkEvents.RequestsAllClients.class, this::getAllConnections);
        new EventPublisher<>(NetworkEvents.ForceCloseAllClients.class, this::shutdownAll);
    }

    private String startClientRequest(Supplier<? extends NetworkingGameClientHandler> handlerFactory,
                                      String ip,
                                      int port) {
        String connectionUuid = UUID.randomUUID().toString();
        try {
            NetworkingClient client = new NetworkingClient(
                    handlerFactory,
                    ip,
                    port);
            this.networkClients.put(connectionUuid, client);
        } catch (Exception e) {
            logger.error(e);
        }
        logger.info("Client {} started", connectionUuid);
        return connectionUuid;
    }

    private void handleStartClientRequest(NetworkEvents.StartClientRequest request) {
        request.future()
                .complete(
                        this.startClientRequest(
                                request.handlerFactory(),
                                request.ip(),
                                request.port())); // TODO: Maybe post ConnectionEstablished event.
    }

    private void handleStartClient(NetworkEvents.StartClient event) {
        String uuid = this.startClientRequest(event.handlerFactory(), event.ip(), event.port());
        new EventPublisher<>(NetworkEvents.StartClientSuccess.class,
                uuid, event.eventId()
        ).asyncPostEvent();
    }

    private void handleCommand(
            NetworkEvents.SendCommand
                    event) { // TODO: Move this to ServerConnection class, keep it internal.
        NetworkingClient client = this.networkClients.get(event.connectionId());
        logger.info("Preparing to send command: {} to server: {}", event.args(), client);
        if (client != null) {
            String args = String.join(" ", event.args()) + "\n";
            client.writeAndFlush(args);
        } else {
            logger.warn("Server {} not found for command '{}'", event.connectionId(), event.args());
        }
    }

    private void handleCloseClient(NetworkEvents.CloseClient event) {
        NetworkingClient client = this.networkClients.get(event.connectionId());
        client.closeConnection(); // TODO: Check if not blocking, what if error, mb not remove?
        this.networkClients.remove(event.connectionId());
        logger.info("Client {} closed successfully.", event.connectionId());
    }

//    private void handleReconnect(Events.ServerEvents.Reconnect event) {
//        NetworkingClient client = this.networkClients.get(event.connectionId());
//        if (client != null) {
//            try {
//                client;
//                logger.info("Server {} reconnected", event.connectionId());
//            } catch (Exception e) {
//                logger.error("Server {} failed to reconnect", event.connectionId(), e);
//                GlobalEventBus.post(new Events.ServerEvents.CouldNotConnect(event.connectionId()));
//            }
//        }
//    } // TODO: Reconnect on disconnect

    //    private void handleChangeConnection(Events.ServerEvents.ChangeConnection event) {
    //        ServerConnection serverConnection = this.serverConnections.get(event.connectionId());
    //        if (serverConnection != null) {
    //            try {
    //                serverConnection.connect(event.ip(), event.port());
    //                logger.info("Server {} changed connection to {}:{}", event.connectionId(),
    // event.ip(), event.port());
    //            } catch (Exception e) {
    //                logger.error("Server {} failed to change connection", event.connectionId(),
    // e);
    //                GlobalEventBus.post(new
    // Events.ServerEvents.CouldNotConnect(event.connectionId()));
    //            }
    //        }
    //    } TODO

    private void getAllConnections(NetworkEvents.RequestsAllClients request) {
        List<NetworkingClient> a = new ArrayList<>(this.networkClients.values());
        request.future().complete(a.toString());
    }

    public void shutdownAll(NetworkEvents.ForceCloseAllClients request) {
        this.networkClients.values().forEach(NetworkingClient::closeConnection);
        this.networkClients.clear();
        logger.info("All servers shut down");
    }
}
