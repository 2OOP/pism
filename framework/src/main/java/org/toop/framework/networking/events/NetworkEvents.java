package org.toop.framework.networking.events;

import org.toop.framework.eventbus.events.EventWithSnowflake;
import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;
import org.toop.framework.networking.NetworkingClient;
import org.toop.framework.networking.NetworkingGameClientHandler;

import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of networking-related event records for use with the {@link org.toop.framework.eventbus.GlobalEventBus}.
 * <p>
 * This class defines all the events that can be posted or listened to in the networking subsystem.
 * Events are separated into those with unique IDs (EventWithSnowflake) and those without (EventWithoutSnowflake).
 * </p>
 */
public class NetworkEvents extends EventsBase {

    /**
     * Requests all active client connections.
     * <p>
     * This is a blocking event. The result will be delivered via the provided {@link CompletableFuture}.
     * </p>
     *
     * @param future CompletableFuture to receive the list of active {@link NetworkingClient} instances.
     */
    public record RequestsAllClients(CompletableFuture<List<NetworkingClient>> future) implements EventWithoutSnowflake {}

    /** Forces all active client connections to close immediately. */
    public record ForceCloseAllClients() implements EventWithoutSnowflake {}

    /** Response indicating a challenge was cancelled. */
    public record ChallengeCancelledResponse(long clientId, String challengeId) implements EventWithoutSnowflake {}

    /** Response indicating a challenge was received. */
    public record ChallengeResponse(long clientId, String challengerName, String gameType, String challengeId)
            implements EventWithoutSnowflake {}

    /** Response containing a list of players for a client. */
    public record PlayerlistResponse(long clientId, String[] playerlist) implements EventWithoutSnowflake {}

    /** Response containing a list of games for a client. */
    public record GamelistResponse(long clientId, String[] gamelist) implements EventWithoutSnowflake {}

    /** Response indicating a game match information for a client. */
    public record GameMatchResponse(long clientId, String playerToMove, String gameType, String opponent)
            implements EventWithoutSnowflake {}

    /** Response indicating the result of a game. */
    public record GameResultResponse(long clientId, String condition) implements EventWithoutSnowflake {}

    /** Response indicating a game move occurred. */
    public record GameMoveResponse(long clientId, String player, String details, String move)
            implements EventWithoutSnowflake {}

    /** Response indicating it is the player's turn. */
    public record YourTurnResponse(long clientId, String message) implements EventWithoutSnowflake {}

    /** Request to send login credentials for a client. */
    public record SendLogin(long clientId, String username) implements EventWithoutSnowflake {}

    /** Request to log out a client. */
    public record SendLogout(long clientId) implements EventWithoutSnowflake {}

    /** Request to retrieve the player list for a client. */
    public record SendGetPlayerlist(long clientId) implements EventWithoutSnowflake {}

    /** Request to retrieve the game list for a client. */
    public record SendGetGamelist(long clientId) implements EventWithoutSnowflake {}

    /** Request to subscribe a client to a game type. */
    public record SendSubscribe(long clientId, String gameType) implements EventWithoutSnowflake {}

    /** Request to make a move in a game. */
    public record SendMove(long clientId, short moveNumber) implements EventWithoutSnowflake {}

    /** Request to challenge another player. */
    public record SendChallenge(long clientId, String usernameToChallenge, String gameType)
            implements EventWithoutSnowflake {}

    /** Request to accept a challenge. */
    public record SendAcceptChallenge(long clientId, int challengeId) implements EventWithoutSnowflake {}

    /** Request to forfeit a game. */
    public record SendForfeit(long clientId) implements EventWithoutSnowflake {}

    /** Request to send a message from a client. */
    public record SendMessage(long clientId, String message) implements EventWithoutSnowflake {}

    /** Request to display help to a client. */
    public record SendHelp(long clientId) implements EventWithoutSnowflake {}

    /** Request to display help for a specific command. */
    public record SendHelpForCommand(long clientId, String command) implements EventWithoutSnowflake {}

    /** Request to close a specific client connection. */
    public record CloseClient(long clientId) implements EventWithoutSnowflake {}

    /**
     * Event to start a new client connection.
     * <p>
     * Carries IP, port, and a unique event ID for correlation with responses.
     * </p>
     *
     * @param ip Server IP address.
     * @param port Server port.
     * @param eventSnowflake Unique event identifier for correlation.
     */
    public record StartClient(String ip, int port, long eventSnowflake) implements EventWithSnowflake {

        @Override
        public Map<String, Object> result() {
            return Stream.of(this.getClass().getRecordComponents())
                    .collect(Collectors.toMap(
                            RecordComponent::getName,
                            rc -> {
                                try {
                                    return rc.getAccessor().invoke(this);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ));
        }

        @Override
        public long eventSnowflake() {
            return this.eventSnowflake;
        }
    }

    /**
     * Response confirming a client was started.
     *
     * @param clientId The client ID assigned to the new connection.
     * @param eventSnowflake Event ID used for correlation.
     */
    public record StartClientResponse(long clientId, long eventSnowflake) implements EventWithSnowflake {
        @Override
        public Map<String, Object> result() {
            return Stream.of(this.getClass().getRecordComponents())
                    .collect(Collectors.toMap(
                            RecordComponent::getName,
                            rc -> {
                                try {
                                    return rc.getAccessor().invoke(this);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ));
        }

        @Override
        public long eventSnowflake() {
            return this.eventSnowflake;
        }
    }

    /** Generic server response. */
    public record ServerResponse(long clientId) implements EventWithoutSnowflake {}

    /**
     * Request to send a command to a server.
     *
     * @param clientId The client connection ID.
     * @param args The command arguments.
     */
    public record SendCommand(long clientId, String... args) implements EventWithoutSnowflake {}

    /** WIP (Not working) Request to reconnect a client to a previous address. */
    public record Reconnect(long clientId) implements EventWithoutSnowflake {}

    /**
     * Response triggered when a message is received from a server.
     *
     * @param clientId The connection ID that received the message.
     * @param message The message content.
     */
    public record ReceivedMessage(long clientId, String message) implements EventWithoutSnowflake {}

    /**
     * Request to change a client connection to a new server.
     *
     * @param clientId The client connection ID.
     * @param ip The new server IP.
     * @param port The new server port.
     */
    public record ChangeClientHost(long clientId, String ip, int port) implements EventWithoutSnowflake {}

    /** WIP (Not working) Response indicating that the client could not connect. */
    public record CouldNotConnect(long clientId) implements EventWithoutSnowflake {}

    /** Event indicating a client connection was closed. */
    public record ClosedConnection(long clientId) implements EventWithoutSnowflake {}
}
