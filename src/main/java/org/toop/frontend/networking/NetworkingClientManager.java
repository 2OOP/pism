package org.toop.frontend.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.eventbus.events.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.eventbus.events.NetworkEvents;

public class NetworkingClientManager {

    private static final Logger logger = LogManager.getLogger(NetworkingClientManager.class);

    /** Map of serverId -> Server instances */
    private final Map<String, NetworkingClient> networkClients = new ConcurrentHashMap<>();

    /** Starts a connection manager, to manage, connections. */
    public NetworkingClientManager() {
        GlobalEventBus.subscribeAndRegister(this::handleStartClientRequest);
        GlobalEventBus.subscribeAndRegister(this::handleStartClient);
        GlobalEventBus.subscribeAndRegister(this::handleCommand);
        GlobalEventBus.subscribeAndRegister(this::handleCloseClient);
//        GlobalEventBus.subscribeAndRegister(
//                Events.ServerEvents.Reconnect.class, this::handleReconnect);
        //        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.ChangeConnection.class,
        // this::handleChangeConnection);
        GlobalEventBus.subscribeAndRegister(this::shutdownAll);
        GlobalEventBus.subscribeAndRegister(this::getAllConnections);
    }

    private String startConnectionRequest(Supplier<? extends NetworkingGameClientHandler> handlerFactory,
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
        return connectionUuid;
    }

    private void handleStartClientRequest(NetworkEvents.StartClientRequest request) {
        request.future()
                .complete(
                        this.startConnectionRequest(
                                request.handlerFactory(),
                                request.ip(),
                                request.port())); // TODO: Maybe post ConnectionEstablished event.
    }

    private void handleStartClient(NetworkEvents.StartClient event) {
        GlobalEventBus.post(
                new NetworkEvents.StartClientSuccess(
                        this.startConnectionRequest(
                                event.handlerFactory(),
                                event.ip(),
                                event.port()),
                        event.ip(),
                        event.port(),
                        event.eventId()
        ));
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
