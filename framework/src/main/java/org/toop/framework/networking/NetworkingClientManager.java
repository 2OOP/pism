package org.toop.framework.networking;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.networking.exceptions.ClientNotFoundException;
import org.toop.framework.networking.exceptions.CouldNotConnectException;
import org.toop.framework.networking.interfaces.NetworkingClient;
import org.toop.framework.networking.types.NetworkingReconnect;

public class NetworkingClientManager implements org.toop.framework.networking.interfaces.NetworkingClientManager {
    private static final Logger logger = LogManager.getLogger(NetworkingClientManager.class);
    private final Map<Long, NetworkingClient> networkClients = new ConcurrentHashMap<>();

    public NetworkingClientManager() {}

    @Override
    public void startClient(
            long id,
            NetworkingClient networkingClient,
            String host, int port,
            NetworkingReconnect networkingReconnect) {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable connectTask = new Runnable() {
            int attempts = 0;

            @Override
            public void run() {
                try {
                    networkingClient.connect(id, host, port);
                    networkClients.put(id, networkingClient);
                    logger.info("New client started successfully for {}:{}", host, port);
                    new EventFlow().addPostEvent(new NetworkEvents.StartClientResponse(id, id)).postEvent();
                    scheduler.shutdown();
                } catch (CouldNotConnectException e) {
                    attempts++;
                    if (attempts < networkingReconnect.reconnectAttempts()) {
                        logger.warn("Could not connect to {}:{}. Retrying in {} {}",
                                host, port, networkingReconnect.timeout(), networkingReconnect.timeUnit());
                        scheduler.schedule(this, networkingReconnect.timeout(), networkingReconnect.timeUnit());
                    } else {
                        logger.error("Failed to start client for {}:{} after {} attempts", host, port, attempts);
                        new EventFlow().addPostEvent(new NetworkEvents.StartClientResponse(-1, id)).postEvent();
                        scheduler.shutdown();
                    }
                } catch (Exception e) {
                    logger.error("Unexpected exception during startClient", e);
                    scheduler.shutdown();
                }
            }
        };

        scheduler.schedule(connectTask, 0, TimeUnit.MILLISECONDS);
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
    public void reconnect(long id, NetworkingReconnect networkingReconnect) throws ClientNotFoundException {
        NetworkingClient client = this.networkClients.get(id);
        if (client == null) {
            throw new ClientNotFoundException(id);
        }

        InetSocketAddress address = client.getAddress();

        if (client.isActive()) {
            client.closeConnection();
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable connectTask = new Runnable() {
            int attempts = 0;

            @Override
            public void run() {
                try {
                    client.connect(id, address.getHostName(), address.getPort());
                    networkClients.put(id, client);
                    logger.info("Client {} reconnected to {}:{}", id, address.getHostName(), address.getPort());
                    new EventFlow().addPostEvent(new NetworkEvents.ReconnectResponse(true, id)).postEvent().postEvent();
                    scheduler.shutdown();
                } catch (CouldNotConnectException e) {
                    attempts++;
                    if (attempts < networkingReconnect.reconnectAttempts()) {
                        logger.warn("Could not reconnect client {} to {}:{}. Retrying in {} {}",
                                id, address.getHostName(), address.getPort(), networkingReconnect.timeout(), networkingReconnect.timeUnit());
                        scheduler.schedule(this, networkingReconnect.timeout(), networkingReconnect.timeUnit());
                    } else {
                        logger.error("Failed to reconnect client {} to {}:{} after {} attempts", id, address.getHostName(), address.getPort(), attempts);
                        new EventFlow().addPostEvent(new NetworkEvents.ReconnectResponse(false, id)).postEvent().postEvent();
                        scheduler.shutdown();
                    }
                } catch (Exception e) {
                    logger.error("Unexpected exception during reconnect for client {}", id, e);
                    new EventFlow().addPostEvent(new NetworkEvents.ReconnectResponse(false, id)).postEvent().postEvent();
                    scheduler.shutdown();
                }
            }
        };

        scheduler.schedule(connectTask, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void changeAddress(long id, String host, int port, NetworkingReconnect networkingReconnect) throws ClientNotFoundException {
        NetworkingClient client = this.networkClients.get(id);
        if (client == null) {
            throw new ClientNotFoundException(id);
        }

        if (client.isActive()) {
            client.closeConnection();
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable connectTask = new Runnable() {
            int attempts = 0;

            @Override
            public void run() {
                try {
                    client.connect(id, host, port);
                    networkClients.put(id, client);
                    logger.info("Client {} changed address to {}:{}", id, host, port);
                    new EventFlow().addPostEvent(new NetworkEvents.ChangeAddressResponse(true, id)).postEvent().postEvent();
                    scheduler.shutdown();
                } catch (CouldNotConnectException e) {
                    attempts++;
                    if (attempts < networkingReconnect.reconnectAttempts()) {
                        logger.warn("Could not connect client {} to {}:{}. Retrying in {} {}",
                                id, host, port, networkingReconnect.timeout(), networkingReconnect.timeUnit());
                        scheduler.schedule(this, networkingReconnect.timeout(), networkingReconnect.timeUnit());
                    } else {
                        logger.error("Failed to connect client {} to {}:{} after {} attempts", id, host, port, attempts);
                        new EventFlow().addPostEvent(new NetworkEvents.ChangeAddressResponse(false, id)).postEvent().postEvent();
                        scheduler.shutdown();
                    }
                } catch (Exception e) {
                    logger.error("Unexpected exception during changeAddress for client {}", id, e);
                    new EventFlow().addPostEvent(new NetworkEvents.ChangeAddressResponse(false, id)).postEvent().postEvent();
                    scheduler.shutdown();
                }
            }
        };

        scheduler.schedule(connectTask, 0, TimeUnit.MILLISECONDS);
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
