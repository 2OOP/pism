package org.toop.framework.networking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.networking.exceptions.ClientNotFoundException;
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
                .listen(this::handleReconnect)
                .listen(this::handleChangeAddress)
                .listen(this::handleGetAllConnections)
                .listen(this::handleShutdownAll);
    }

    void handleStartClient(NetworkEvents.StartClient event) {
        long clientId = SnowflakeGenerator.nextId();
        clientManager.startClient(
                clientId,
                event.networkingClient(),
                event.networkingConnector(),
                () -> new EventFlow().addPostEvent(new NetworkEvents.StartClientResponse(clientId, true, event.identifier())).postEvent(),
                () -> new EventFlow().addPostEvent(new NetworkEvents.StartClientResponse(clientId, false, event.identifier())).postEvent()
        );
    }

    private void sendCommand(long clientId, String command) {
        try {
            clientManager.sendCommand(clientId, command);
        } catch (ClientNotFoundException e) {
            logger.error(e);
        }
    }

    private void handleCommand(NetworkEvents.SendCommand event) {
        String args = String.join(" ", event.args());
        sendCommand(event.clientId(), args);
    }

    private void handleSendLogin(NetworkEvents.SendLogin event) {
        sendCommand(event.clientId(), String.format("LOGIN %s", event.username()));
    }

    private void handleSendLogout(NetworkEvents.SendLogout event) {
        sendCommand(event.clientId(), "LOGOUT");
    }

    private void handleSendGetPlayerlist(NetworkEvents.SendGetPlayerlist event) {
        sendCommand(event.clientId(), "GET PLAYERLIST");
    }

    private void handleSendGetGamelist(NetworkEvents.SendGetGamelist event) {
        sendCommand(event.clientId(), "GET GAMELIST");
    }

    private void handleSendSubscribe(NetworkEvents.SendSubscribe event) {
        sendCommand(event.clientId(), String.format("SUBSCRIBE %s", event.gameType()));
    }

    private void handleSendMove(NetworkEvents.SendMove event) {
        sendCommand(event.clientId(), String.format("MOVE %d", event.moveNumber()));
    }

    private void handleSendChallenge(NetworkEvents.SendChallenge event) {
        sendCommand(event.clientId(), String.format("CHALLENGE %s %s", event.usernameToChallenge(), event.gameType()));
    }

    private void handleSendAcceptChallenge(NetworkEvents.SendAcceptChallenge event) {
        sendCommand(event.clientId(), String.format("CHALLENGE ACCEPT %d", event.challengeId()));
    }

    private void handleSendForfeit(NetworkEvents.SendForfeit event) {
        sendCommand(event.clientId(), "FORFEIT");
    }

    private void handleSendMessage(NetworkEvents.SendMessage event) {
        sendCommand(event.clientId(), String.format("MESSAGE %s", event.message()));
    }

    private void handleSendHelp(NetworkEvents.SendHelp event) {
        sendCommand(event.clientId(), "HELP");
    }

    private void handleSendHelpForCommand(NetworkEvents.SendHelpForCommand event) {
        sendCommand(event.clientId(), String.format("HELP %s", event.command()));
    }

    private void handleReconnect(NetworkEvents.Reconnect event) {
        clientManager.startClient(
                event.clientId(),
                event.networkingClient(),
                event.networkingConnector(),
                () -> new EventFlow().addPostEvent(new NetworkEvents.ReconnectResponse(true, event.identifier())).postEvent(),
                () -> new EventFlow().addPostEvent(new NetworkEvents.ReconnectResponse(false, event.identifier())).postEvent()
        );
    }

    private void handleChangeAddress(NetworkEvents.ChangeAddress event) {
        clientManager.startClient(
                event.clientId(),
                event.networkingClient(),
                event.networkingConnector(),
                () -> new EventFlow().addPostEvent(new NetworkEvents.ChangeAddressResponse(true, event.identifier())).postEvent(),
                () -> new EventFlow().addPostEvent(new NetworkEvents.ChangeAddressResponse(false, event.identifier())).postEvent()
        );
    }

    void handleCloseClient(NetworkEvents.CloseClient event) {
        try {
            this.clientManager.closeClient(event.clientId());
        } catch (ClientNotFoundException e) {
            logger.error(e);
        }
    }

    void handleGetAllConnections(NetworkEvents.RequestsAllClients request) {
//        List<NetworkingClient> a = new ArrayList<>(this.networkClients.values());
//        request.future().complete(a);
        // TODO
    }

    public void handleShutdownAll(NetworkEvents.ForceCloseAllClients request) {
        // TODO
    }
}
