package org.toop.framework.networking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.networking.interfaces.NetworkingClient;

public class NetworkingClientManager implements org.toop.framework.networking.interfaces.NetworkingClientManager {
    private static final Logger logger = LogManager.getLogger(NetworkingClientManager.class);
    private final Map<Long, NetworkingClient> networkClients = new ConcurrentHashMap<>();

    public NetworkingClientManager() {}

    @Override
    public OptionalLong startClient(long id, NetworkingClient networkingClient, String host, int port) {
        try {
            networkingClient.connect(id, host, port);
            this.networkClients.put(id, networkingClient);
            logger.info("New client started successfully for {}:{}", host, port);
        } catch (Exception e) {
            logger.error(e); // TODO Better error handling
            return OptionalLong.empty();
        }
        return OptionalLong.of(id);
    }

    @Override
    public boolean sendCommand(long id, String command) {
        logger.info("Sending command to client for {}:{}", id, command);
        if (command.isEmpty()) { return false; }

        NetworkingClient client = this.networkClients.get(id);
        if (client == null) { return false; } // TODO: Create client not found exceptions.

        String toSend = command.trim();

        if (toSend.endsWith("\n")) { client.writeAndFlush(toSend); }
        else { client.writeAndFlush(toSend + "\n"); }

        return true;
    }

    @Override
    public boolean reconnect(long id) {
        return false; // TODO
    }

    @Override
    public boolean changeAddress(long id, String host, int port) {
        return false; // TODO
    }

    @Override
    public boolean closeClient(long id) {
        return false; // TODO
    }
}
