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

public class NetworkEvents extends EventsBase {

    /**
     * BLOCKING Requests all active connections. The result is returned via the provided
     * CompletableFuture.
     *
     * @param future List of all connections in string form.
     */
    public record RequestsAllClients(CompletableFuture<List<NetworkingClient>> future) implements EventWithoutSnowflake {}

    /** Forces closing all active connections immediately. */
    public record ForceCloseAllClients() implements EventWithoutSnowflake {}

    public record PlayerListResponse(long clientId, String[] playerlist) implements EventWithoutSnowflake {}

    public record CloseClient(long connectionId) implements EventWithoutSnowflake {}

    /**
     * Event to start a new client connection to a server.
     * <p>
     * This event is typically posted to the {@code GlobalEventBus} to initiate the creation of
     * a client connection, and carries all information needed to establish that connection:
     * <br>
     * - A factory for creating the Netty handler that will manage the connection
     * <br>
     * - The server's IP address and port
     * <br>
     * - A unique event identifier for correlation with follow-up events
     * </p>
     *
     * <p>
     * The {@link #eventSnowflake()} allows callers to correlate the {@code StartClient} event
     * with subsequent success/failure events. For example, a {@code StartClientSuccess}
     * or {@code StartClientFailure} event may carry the same {@code eventId}.
     * </p>
     *
     * @param ip             The IP address of the server to connect to.
     * @param port           The port number of the server to connect to.
     * @param eventSnowflake        A unique identifier for this event, typically injected
     *                       automatically by the {@link org.toop.framework.eventbus.EventFlow}.
     */
    public record StartClient(
            String ip,
            int port,
            long eventSnowflake
    ) implements EventWithSnowflake {

        /**
         * Returns a map representation of this event, where keys are record component names
         * and values are their corresponding values. Useful for generic logging, debugging,
         * or serializing events without hardcoding field names.
         *
         * @return a {@code Map<String, Object>} containing field names and values
         */
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

        /**
         * Returns the unique event identifier used for correlating this event.
         *
         * @return the event ID string
         */
        @Override
        public long eventSnowflake() {
            return this.eventSnowflake;
        }
    }

    /**
     *
     * @param clientId The ID of the client to be used in requests.
     * @param eventSnowflake The eventID used in checking if event is for you.
     */
    public record StartClientResponse(long clientId, long eventSnowflake)
            implements EventWithSnowflake {
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
     *
     * @param clientId The ID of the client that received the response.
     */
    public record ServerResponse(long clientId) implements EventWithoutSnowflake {}

    /**
     * Triggers sending a command to a server.
     *
     * @param connectionId The UUID of the connection to send the command on.
     * @param args The command arguments.
     */
    public record SendCommand(long connectionId, String... args) implements EventWithoutSnowflake {}
    /**
     * Triggers reconnecting to a previous address.
     *
     * @param connectionId The identifier of the connection being reconnected.
     */
    public record Reconnect(long connectionId) implements EventWithoutSnowflake {}


    /**
     * Triggers when the server client receives a message.
     *
     * @param ConnectionId The snowflake id of the connection that received the message.
     * @param message The message received.
     */
    public record ReceivedMessage(long ConnectionId, String message) implements EventWithoutSnowflake {}

    /**
     * Triggers changing connection to a new address.
     *
     * @param connectionId The identifier of the connection being changed.
     * @param ip The new IP address.
     * @param port The new port.
     */
    public record ChangeClient(long connectionId, String ip, int port) implements EventWithoutSnowflake {}


    /**
     * Triggers when the server couldn't connect to the desired address.
     *
     * @param connectionId The identifier of the connection that failed.
     */
    public record CouldNotConnect(long connectionId) implements EventWithoutSnowflake {}

    /** WIP Triggers when a connection closes. */
    public record ClosedConnection() implements EventWithoutSnowflake {}

}
