package org.toop.framework.networking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.interfaces.NetworkingClient;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.networking.interfaces.NetworkingClientManager;

public class NetworkingClientEventListener {

    private static final Logger logger = LogManager.getLogger(NetworkingClientEventListener.class);
    private final NetworkingClientManager clientManager;

    /** Starts a connection manager, to manage, connections. */
    public NetworkingClientEventListener(NetworkingClientManager clientManager) {
        this.clientManager = clientManager;
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
    }

    void handleStartClient(NetworkEvents.StartClient<? extends NetworkingClient> event) {
        long clientId = clientManager.startClient(event.networkingClientClass(), event.host(), event.port()).orElse(-1);
        logger.info("Client {} started", clientId);
        try {
            new EventFlow()
                    .addPostEvent(new NetworkEvents.StartClientResponse(clientId, event.getIdentifier()))
                    .asyncPostEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleCommand(NetworkEvents.SendCommand event) {
        String args = String.join(" ", event.args());
        logger.error(args);
        clientManager.sendCommand(event.clientId(), args);
    }

    void handleSendLogin(NetworkEvents.SendLogin event) {
        logger.error("{}", event.username());
        clientManager.sendCommand(event.clientId(), String.format("LOGIN %s", event.username()));
    }

    private void handleSendLogout(NetworkEvents.SendLogout event) {
        clientManager.sendCommand(event.clientId(), "LOGOUT");
    }

    private void handleSendGetPlayerlist(NetworkEvents.SendGetPlayerlist event) {
        clientManager.sendCommand(event.clientId(), "GET PLAYERLIST");
    }

    private void handleSendGetGamelist(NetworkEvents.SendGetGamelist event) {
        clientManager.sendCommand(event.clientId(), "GET GAMELIST");
    }

    private void handleSendSubscribe(NetworkEvents.SendSubscribe event) {
        clientManager.sendCommand(event.clientId(), String.format("SUBSCRIBE %s", event.gameType()));
    }

    private void handleSendMove(NetworkEvents.SendMove event) {
        clientManager.sendCommand(event.clientId(), String.format("MOVE %d", event.moveNumber()));
    }

    private void handleSendChallenge(NetworkEvents.SendChallenge event) {
        clientManager.sendCommand(
                event.clientId(),
                String.format("CHALLENGE %s %s", event.usernameToChallenge(), event.gameType()));
    }

    private void handleSendAcceptChallenge(NetworkEvents.SendAcceptChallenge event) {
        clientManager.sendCommand(event.clientId(), String.format("CHALLENGE ACCEPT %d", event.challengeId()));
    }

    private void handleSendForfeit(NetworkEvents.SendForfeit event) {
        clientManager.sendCommand(event.clientId(), "FORFEIT");
    }

    private void handleSendMessage(NetworkEvents.SendMessage event) {
        clientManager.sendCommand(event.clientId(), String.format("MESSAGE %s", event.message()));
    }

    private void handleSendHelp(NetworkEvents.SendHelp event) {
        clientManager.sendCommand(event.clientId(), "HELP");
    }

    private void handleSendHelpForCommand(NetworkEvents.SendHelpForCommand event) {
        clientManager.sendCommand(event.clientId(), String.format("HELP %s", event.command()));
    }

    private void handleChangeClientHost(NetworkEvents.ChangeClientHost event) {
//        NetworkingClient client = this.networkClients.get(event.clientId());
//        client.closeConnection();
//        startClientRequest(event.ip(), event.port(), event.clientId());
    }

    void handleCloseClient(NetworkEvents.CloseClient event) {
        this.clientManager.closeClient(event.clientId());
    }

    void handleGetAllConnections(NetworkEvents.RequestsAllClients request) {
//        List<NetworkingClient> a = new ArrayList<>(this.networkClients.values());
//        request.future().complete(a);
    }

    public void handleShutdownAll(NetworkEvents.ForceCloseAllClients request) {
        // TODO
    }
}
