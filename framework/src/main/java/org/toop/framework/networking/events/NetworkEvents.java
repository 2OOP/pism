package org.toop.framework.networking.events;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.toop.framework.eventbus.events.GenericEvent;
import org.toop.framework.eventbus.events.ResponseToUniqueEvent;
import org.toop.framework.eventbus.events.UniqueEvent;
import org.toop.framework.eventbus.events.EventsBase;
import org.toop.annotations.AutoResponseResult;
import org.toop.framework.networking.interfaces.NetworkingClient;
import org.toop.framework.networking.types.NetworkingConnector;

/**
 * A collection of networking-related event records for use with the {@link
 * org.toop.framework.eventbus.GlobalEventBus}.
 *
 * <p>This class defines all the events that can be posted or listened to in the networking
 * subsystem. Events are separated into those with unique IDs (UniqueEvent) and those without
 * (GenericEvent).
 */
public class NetworkEvents extends EventsBase {

    /**
     * Requests all active client connections.
     *
     * <p>This is a blocking event. The result will be delivered via the provided {@link
     * CompletableFuture}.
     *
     * @param future CompletableFuture to receive the list of active {@link NetworkingClient}
     *     instances.
     */
    public record RequestsAllClients(CompletableFuture<List<NetworkingClient>> future)
            implements GenericEvent {}

    /** Forces all active client connections to close immediately. */
    public record ForceCloseAllClients() implements GenericEvent {}

    /** Response indicating a challenge was cancelled. */
    public record ChallengeCancelledResponse(long clientId, String challengeId) implements GenericEvent {}

    /** Response indicating a challenge was received. */
    public record ChallengeResponse(long clientId, String challengerName, String challengeId, String gameType)
            implements GenericEvent {}

    /** Response containing a list of players for a client. */
    public record PlayerlistResponse(long clientId, String[] playerlist) implements GenericEvent {}

    /** Response containing a list of games for a client. */
    public record GamelistResponse(long clientId, String[] gamelist) implements GenericEvent {}

    /** Response indicating a game match information for a client. */
    public record GameMatchResponse(long clientId, String playerToMove, String gameType, String opponent)
            implements GenericEvent {}

    /** Response indicating the result of a game. */
    public record GameResultResponse(long clientId, String condition) implements GenericEvent {}

    /** Response indicating a game move occurred. */
    public record GameMoveResponse(long clientId, String player, String move, String details) implements GenericEvent {}

    /** Response indicating it is the player's turn. */
    public record YourTurnResponse(long clientId, String message)
            implements GenericEvent {}

    /** Request to send login credentials for a client. */
    public record SendLogin(long clientId, String username) implements GenericEvent {}

    /** Request to log out a client. */
    public record SendLogout(long clientId) implements GenericEvent {}

    /** Request to retrieve the player list for a client. */
    public record SendGetPlayerlist(long clientId) implements GenericEvent {}

    /** Request to retrieve the game list for a client. */
    public record SendGetGamelist(long clientId) implements GenericEvent {}

    /** Request to subscribe a client to a game type. */
    public record SendSubscribe(long clientId, String gameType) implements GenericEvent {}

    /** Request to make a move in a game. */
    public record SendMove(long clientId, short moveNumber) implements GenericEvent {}

    /** Request to challenge another player. */
    public record SendChallenge(long clientId, String usernameToChallenge, String gameType) implements GenericEvent {}

    /** Request to accept a challenge. */
    public record SendAcceptChallenge(long clientId, int challengeId) implements GenericEvent {}

    /** Request to forfeit a game. */
    public record SendForfeit(long clientId) implements GenericEvent {}

    /** Request to send a message from a client. */
    public record SendMessage(long clientId, String message) implements GenericEvent {}

    /** Request to display help to a client. */
    public record SendHelp(long clientId) implements GenericEvent {}

    /** Request to display help for a specific command. */
    public record SendHelpForCommand(long clientId, String command) implements GenericEvent {}

    /** Request to close a specific client connection. */
    public record CloseClient(long clientId) implements GenericEvent {}

    /** Generic server response. */
    public record ServerResponse(long clientId) implements GenericEvent {}

    /**
     * Request to send a command to a server.
     *
     * @param clientId The client connection ID.
     * @param args The command arguments.
     */
    public record SendCommand(long clientId, String... args) implements GenericEvent {}

    /**
     * Event to start a new client connection.
     *
     * <p>Carries IP, port, and a unique event ID for correlation with responses.
     *
     * @param networkingClient
     * @param networkingConnector
     */
    public record StartClient(
            NetworkingClient networkingClient,
            NetworkingConnector networkingConnector,
            long identifier) implements UniqueEvent {}

    /**
     * Response confirming a client was started.
     *
     * @param clientId   The client ID assigned to the new connection.
     * @param successful If successfully connected or not. If not clientId will also be -1.
     */
    @AutoResponseResult
    public record StartClientResponse(long clientId, boolean successful, long identifier) implements ResponseToUniqueEvent {}

    /** WIP (Not working) Request to reconnect a client to a previous address. */
    public record Reconnect(long clientId, NetworkingClient networkingClient, NetworkingConnector networkingConnector, long identifier)
            implements UniqueEvent {}

    public record ReconnectResponse(boolean successful, long identifier) implements ResponseToUniqueEvent {}

    /**
     * Request to change a client connection to a new server.
     *
     * @param clientId The client connection ID.
     * @param networkingConnector
     */
    public record ChangeAddress(long clientId, NetworkingClient networkingClient, NetworkingConnector networkingConnector, long identifier)
            implements UniqueEvent {}

    public record ChangeAddressResponse(boolean successful, long identifier) implements ResponseToUniqueEvent {}

    /**
     * Response triggered when a message is received from a server.
     *
     * @param clientId The connection ID that received the message.
     * @param message The message content.
     */
    public record ReceivedMessage(long clientId, String message) implements GenericEvent {}

    /** Event indicating a client connection was closed. */
    public record ClosedConnection(long clientId) implements GenericEvent {}
}
