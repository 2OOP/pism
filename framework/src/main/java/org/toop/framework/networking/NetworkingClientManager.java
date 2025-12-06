package org.toop.framework.networking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.eventbus.bus.EventBus;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.networking.exceptions.ClientNotFoundException;
import org.toop.framework.networking.exceptions.CouldNotConnectException;
import org.toop.framework.networking.interfaces.NetworkingClient;
import org.toop.framework.networking.types.NetworkingConnector;

public class NetworkingClientManager implements org.toop.framework.networking.interfaces.NetworkingClientManager {
    private static final Logger logger = LogManager.getLogger(NetworkingClientManager.class);

    private final EventBus eventBus;
    private final Map<Long, NetworkingClient> networkClients = new ConcurrentHashMap<>();

    public NetworkingClientManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private void connectHelper(
            long id,
            NetworkingClient nClient,
            NetworkingConnector nConnector,
            Runnable onSuccess,
            Runnable onFailure
    ) {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        EventFlow closeEvent = new EventFlow()
                .listen(
                    NetworkEvents.CloseClient.class,
                    e -> {
                            if (e.clientId() == id) scheduler.shutdownNow();
                        }, "close");

        Runnable connectTask = new Runnable() {
            int attempts = 0;

            @Override
            public void run() {

                NetworkingClient qClient = networkClients.get(id);
                if (qClient != null) {
                    qClient.closeConnection();
                    networkClients.remove(id);
                }

                try {
                    nClient.connect(id, nConnector.host(), nConnector.port());
                    networkClients.put(id, nClient);
                    logger.info("New client started successfully for {}:{}", nConnector.host(), nConnector.port());
                    eventBus.post(new NetworkEvents.ConnectTry(id, attempts, nConnector.reconnectAttempts(), true));
                    onSuccess.run();
                    scheduler.shutdown();
                } catch (CouldNotConnectException e) {
                    attempts++;
                    if (attempts < nConnector.reconnectAttempts()) {
                        logger.warn("Could not connect to {}:{}. Retrying in {} {}",
                                nConnector.host(), nConnector.port(), nConnector.timeout(), nConnector.timeUnit());
                        eventBus.post(new NetworkEvents.ConnectTry(id, attempts, nConnector.reconnectAttempts(), false));
                        scheduler.schedule(this, nConnector.timeout(), nConnector.timeUnit());
                    } else {
                        logger.error("Failed to start client for {}:{} after {} attempts", nConnector.host(), nConnector.port(), attempts);
                        eventBus.post(new NetworkEvents.ConnectTry(id, -1, nConnector.reconnectAttempts(), false));
                        onFailure.run();
                        scheduler.shutdown();
                    }
                } catch (Exception e) {
                    logger.error("Unexpected exception during startClient", e);
                    eventBus.post(new NetworkEvents.ConnectTry(id, -1, nConnector.reconnectAttempts(), false));
                    onFailure.run();
                    scheduler.shutdown();
                }
            }
        };

        scheduler.schedule(connectTask, 0, TimeUnit.MILLISECONDS);
//
//        closeEvent.unsubscribe("close");
    }

    @Override
    public void startClient(
            long id,
            NetworkingClient nClient,
            NetworkingConnector nConnector,
            Runnable onSuccess,
            Runnable onFailure
    ) {
        connectHelper(
            id,
            nClient,
            nConnector,
            onSuccess,
            onFailure
        );
    }

    @Override
    public void sendCommand(long id, String command) throws ClientNotFoundException {
        logger.info("Sending command to client for {}:{}", id, command);
        if (command.isEmpty()) {
            IllegalArgumentException e = new IllegalArgumentException("command is empty");
            logger.error("Invalid command received", e);
            return;
        }

        NetworkingClient client = this.networkClients.get(id);
        if (client == null) {
            throw new ClientNotFoundException(id);
        }

        String toSend = command.trim();

        if (toSend.endsWith("\n")) { client.writeAndFlush(toSend); }
        else { client.writeAndFlush(toSend + "\n"); }

    }

    @Override
    public void closeClient(long id) throws ClientNotFoundException {
        NetworkingClient client = this.networkClients.get(id);
        if (client == null) {
            throw new ClientNotFoundException(id);
        }

        client.closeConnection();

    }
}
