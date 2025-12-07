package org.toop.framework.networking.events;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.toop.annotations.AutoResponseResult;
import org.toop.framework.eventbus.GlobalEventBus;
import org.toop.framework.eventbus.events.*;
import org.toop.framework.networking.interfaces.NetworkingClient;
import org.toop.framework.networking.types.NetworkingConnector;

/**
 * Defines all event types related to the networking subsystem.
 * <p>
 * These events are used in conjunction with the {@link GlobalEventBus}
 * and {@link org.toop.framework.eventbus.EventFlow} to communicate between components
 * such as networking clients, managers, and listeners.
 * </p>
 *
 * <h2>Important</h2>
 * For all {@link UniqueEvent} and {@link ResponseToUniqueEvent} types:
 * the {@code identifier} field is automatically generated and injected
 * by {@link org.toop.framework.eventbus.EventFlow}. It should <strong>never</strong>
 * be manually assigned by user code. (Exceptions may apply)
 */
public class NetworkEvents extends EventsBase {

    // ------------------------------------------------------
    // Generic Request & Response Events (no identifier)
    // ------------------------------------------------------

    /**
     * Requests a list of all active networking clients.
     * <p>
     * This is a blocking request that returns the list asynchronously
     * via the provided {@link CompletableFuture}.
     */
    public record RequestsAllClients(CompletableFuture<List<NetworkingClient>> future)
            implements GenericEvent {}

    /** Signals all active clients should be forcefully closed. */
    public record ForceCloseAllClients() implements GenericEvent {}

    /** Indicates a challenge was cancelled by the server. */
    public record ChallengeCancelledResponse(long clientId, String challengeId)
            implements GenericEvent {}

    /** Indicates an incoming challenge from another player. */
    public record ChallengeResponse(long clientId, String challengerName, String challengeId, String gameType)
            implements GenericEvent {}

    /** Contains the list of players currently available on the server. */
    public record PlayerlistResponse(long clientId, String[] playerlist)
            implements GenericEvent {}

    /** Contains the list of available game types for a client. */
    public record GamelistResponse(long clientId, String[] gamelist)
            implements GenericEvent {}

    /** Provides match information when a new game starts. */
    public record GameMatchResponse(long clientId, String playerToMove, String gameType, String opponent)
            implements GenericEvent {}

    /** Indicates the outcome or completion of a game. */
    public record GameResultResponse(long clientId, String condition)
            implements GenericEvent {}

    /** Indicates that a game move has been processed or received. */
    public record GameMoveResponse(long clientId, String player, String move, String details)
            implements GenericEvent {}

    /** Indicates it is the current player's turn to move. */
    public record YourTurnResponse(long clientId, String message)
            implements GenericEvent {}

    /** Requests a login operation for the given client. */
    public record SendLogin(long clientId, String username)
            implements GenericEvent {}

    /** Requests logout for the specified client. */
    public record SendLogout(long clientId)
            implements GenericEvent {}

    /** Requests the player list from the server. */
    public record SendGetPlayerlist(long clientId)
            implements GenericEvent {}

    /** Requests the game list from the server. */
    public record SendGetGamelist(long clientId)
            implements GenericEvent {}

    /** Requests a subscription to updates for a given game type. */
    public record SendSubscribe(long clientId, String gameType)
            implements GenericEvent {}

    /** Sends a game move command to the server. */
    public record SendMove(long clientId, short moveNumber)
            implements GenericEvent {}

    /** Requests to challenge another player to a game. */
    public record SendChallenge(long clientId, String usernameToChallenge, String gameType)
            implements GenericEvent {}

    /** Requests to accept an existing challenge. */
    public record SendAcceptChallenge(long clientId, int challengeId)
            implements GenericEvent {}

    /** Requests to forfeit the current game. */
    public record SendForfeit(long clientId)
            implements GenericEvent {}

    /** Sends a chat or informational message from a client. */
    public record SendMessage(long clientId, String message)
            implements GenericEvent {}

    /** Requests general help information from the server. */
    public record SendHelp(long clientId)
            implements GenericEvent {}

    /** Requests help information specific to a given command. */
    public record SendHelpForCommand(long clientId, String command)
            implements GenericEvent {}

    /** Requests to close an active client connection. */
    public record CloseClient(long clientId)
            implements GenericEvent {}

    /** A generic event indicating a raw server response. */
    public record ServerResponse(long clientId)
            implements GenericEvent {}

    /**
     * Sends a raw command string to the server.
     *
     * @param clientId The client ID to send the command from.
     * @param args The command arguments.
     */
    public record SendCommand(long clientId, String... args)
            implements GenericEvent {}

    /** Event fired when a message is received from the server. */
    public record ReceivedMessage(long clientId, String message)
            implements GenericEvent {}

    /** Indicates that a client connection has been closed. */
    public record ClosedConnection(long clientId)
            implements GenericEvent {}

    // ------------------------------------------------------
    // Unique Request & Response Events (with identifier)
    // ------------------------------------------------------

    /**
     * Requests creation and connection of a new client.
     * <p>
     * The {@code identifier} is automatically assigned by {@link org.toop.framework.eventbus.EventFlow}
     * to correlate with its corresponding {@link StartClientResponse}.
     * </p>
     *
     * @param networkingClient The client instance to start.
     * @param networkingConnector Connection details (host, port, etc.).
     * @param identifier Automatically injected unique identifier.
     */
    public record StartClient(
            NetworkingClient networkingClient,
            NetworkingConnector networkingConnector,
            long identifier)
            implements UniqueEvent {}

    public record CreatedIdForClient(long clientId, long identifier) implements ResponseToUniqueEvent {}

    public record ConnectTry(long clientId, int amount, int maxAmount, boolean success) implements GenericEvent {}

    /**
     * Response confirming that a client has been successfully started.
     * <p>
     * The {@code identifier} value is automatically propagated from
     * the original {@link StartClient} request by {@link org.toop.framework.eventbus.EventFlow}.
     * </p>
     *
     * @param clientId The newly assigned client ID.
     * @param successful Whether the connection succeeded.
     * @param identifier Automatically injected correlation ID.
     */
    @AutoResponseResult
    public record StartClientResponse(long clientId, boolean successful, long identifier)
            implements ResponseToUniqueEvent {}

    /**
     * Requests reconnection of an existing client using its previous configuration.
     * <p>
     * The {@code identifier} is automatically injected by {@link org.toop.framework.eventbus.EventFlow}.
     * </p>
     */
    public record Reconnect(
            long clientId,
            NetworkingClient networkingClient,
            NetworkingConnector networkingConnector,
            long identifier)
            implements UniqueEvent {}

    /** Response to a {@link Reconnect} event, carrying the success result. */
    public record ReconnectResponse(boolean successful, long identifier)
            implements ResponseToUniqueEvent {}

    /**
     * Requests to change the connection target (host/port) for a client.
     * <p>
     * The {@code identifier} is automatically injected by {@link org.toop.framework.eventbus.EventFlow}.
     * </p>
     */
    public record ChangeAddress(
            long clientId,
            NetworkingClient networkingClient,
            NetworkingConnector networkingConnector,
            long identifier)
            implements UniqueEvent {}

    /** Response to a {@link ChangeAddress} event, carrying the success result. */
    public record ChangeAddressResponse(boolean successful, long identifier)
            implements ResponseToUniqueEvent {}
}
