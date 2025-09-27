package org.toop.framework.networking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.networking.events.NetworkEvents;

public class NetworkingClientManager {

    private static final Logger logger = LogManager.getLogger(NetworkingClientManager.class);

    /** Map of serverId -> Server instances */
    private final Map<Long, NetworkingClient> networkClients = new ConcurrentHashMap<>();

    /** Starts a connection manager, to manage, connections. */
    public NetworkingClientManager() throws NetworkingInitializationException {
        try {
            new EventFlow()
                    .listen(this::handleStartClient)
                    .listen(this::handleCommand)
                    .listen(this::handleCloseClient)
                    .listen(this::getAllConnections)
                    .listen(this::shutdownAll);
            logger.info("NetworkingClientManager initialized");
        }  catch (Exception e) {
            logger.error("Failed to initialize the client manager", e);
            throw e;
        }
    }

    private long startClientRequest(String ip, int port) {
        long connectionId = new SnowflakeGenerator().nextId(); // TODO: Maybe use the one generated
        try {                                                            //       With EventFlow
            NetworkingClient client = new NetworkingClient(
                    () -> new NetworkingGameClientHandler(connectionId),
                    ip,
                    port,
                    connectionId);
            client.setConnectionId(connectionId);
            this.networkClients.put(connectionId, client);
        } catch (Exception e) {
            logger.error(e);
        }
        logger.info("Client {} started", connectionId);
        return connectionId;
    }

    private void handleStartClient(NetworkEvents.StartClient event) {
        long id = this.startClientRequest(event.ip(), event.port());
        new Thread(() -> {
            try {
                Thread.sleep(100); // TODO: Is this a good idea?
                new EventFlow().addPostEvent(NetworkEvents.StartClientResponse.class,
                        id, event.eventSnowflake()
                ).asyncPostEvent();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void handleCommand(
            NetworkEvents.SendCommand
                    event) { // TODO: Move this to ServerConnection class, keep it internal.
        NetworkingClient client = this.networkClients.get(event.connectionId());
        logger.info("Preparing to send command: {} to server: {}", event.args(), client.getId());
        String args = String.join(" ", event.args());
        client.writeAndFlushnl(args);
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
        request.future().complete(a);
    }

    public void shutdownAll(NetworkEvents.ForceCloseAllClients request) {
        this.networkClients.values().forEach(NetworkingClient::closeConnection);
        this.networkClients.clear();
        logger.info("All servers shut down");
    }
}
