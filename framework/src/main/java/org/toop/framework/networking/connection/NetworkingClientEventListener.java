package org.toop.framework.networking.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.eventbus.bus.EventBus;
import org.toop.framework.networking.connection.events.NetworkEvents;
import org.toop.framework.networking.connection.exceptions.ClientNotFoundException;
import org.toop.framework.networking.connection.interfaces.NetworkingClientManager;

public class NetworkingClientEventListener {
    private static final Logger logger = LogManager.getLogger(NetworkingClientEventListener.class);

    private final NetworkingClientManager clientManager;

    /** Starts a connection manager, to manage, connections. */
    public NetworkingClientEventListener(EventBus eventBus, NetworkingClientManager clientManager) {
        this.clientManager = clientManager;
        new EventFlow(eventBus)
                .listen(NetworkEvents.StartClient.class, this::handleStartClient, false)
                .listen(NetworkEvents.SendCommand.class, this::handleCommand, false)
                .listen(NetworkEvents.SendLogin.class, this::handleSendLogin, false)
                .listen(NetworkEvents.SendLogout.class, this::handleSendLogout, false)
                .listen(NetworkEvents.SendGetPlayerlist.class, this::handleSendGetPlayerlist, false)
                .listen(NetworkEvents.SendGetGamelist.class, this::handleSendGetGamelist, false)
                .listen(NetworkEvents.SendSubscribe.class, this::handleSendSubscribe, false)
                .listen(NetworkEvents.SendMove.class, this::handleSendMove, false)
                .listen(NetworkEvents.SendChallenge.class, this::handleSendChallenge, false)
                .listen(NetworkEvents.SendAcceptChallenge.class, this::handleSendAcceptChallenge, false)
                .listen(NetworkEvents.SendForfeit.class, this::handleSendForfeit, false)
                .listen(NetworkEvents.SendMessage.class, this::handleSendMessage, false)
                .listen(NetworkEvents.SendHelp.class, this::handleSendHelp, false)
                .listen(NetworkEvents.SendHelpForCommand.class, this::handleSendHelpForCommand, false)
                .listen(NetworkEvents.CloseClient.class, this::handleCloseClient, false)
                .listen(NetworkEvents.Reconnect.class, this::handleReconnect, false)
                .listen(NetworkEvents.ChangeAddress.class, this::handleChangeAddress, false)
                .listen(NetworkEvents.RequestsAllClients.class, this::handleGetAllConnections, false)
                .listen(NetworkEvents.ForceCloseAllClients.class, this::handleShutdownAll, false);
    }

    void handleStartClient(NetworkEvents.StartClient event) {
        long clientId = SnowflakeGenerator.nextId();
        new EventFlow().addPostEvent(new NetworkEvents.CreatedIdForClient(clientId, event.identifier())).postEvent();
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
