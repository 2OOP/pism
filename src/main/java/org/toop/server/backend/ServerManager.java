package org.toop.server.backend;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;
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
    }

    private String startServer(String port) {
        String serverId = UUID.randomUUID().toString();
        try {
            TcpServer server = new TcpServer(Integer.parseInt(port));
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
        request.future().complete(this.startServer(request.port())); // TODO: Maybe post StartServer event.
    }

    private void handleStartServer(Events.ServerEvents.StartServer event) {
        GlobalEventBus.post(new Events.ServerEvents.ServerStarted(
                this.startServer(event.port()),
                event.port()
        ));
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
