package org.toop.framework.eventbus.events;

import org.toop.framework.networking.NetworkingGameClientHandler;

import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetworkEvents extends Events {

    /**
     * BLOCKING Requests all active connections. The result is returned via the provided
     * CompletableFuture.
     *
     * @param future List of all connections in string form.
     */
    public record RequestsAllClients(CompletableFuture<String> future) implements EventWithoutUuid {}

    /** Forces closing all active connections immediately. */
    public record ForceCloseAllClients() implements EventWithoutUuid {}

    public record CloseClientRequest(CompletableFuture<String> future) implements EventWithoutUuid {}

    public record CloseClient(String connectionId) implements EventWithoutUuid {}

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
     * The {@link #eventId()} allows callers to correlate the {@code StartClient} event
     * with subsequent success/failure events. For example, a {@code StartClientSuccess}
     * or {@code StartClientFailure} event may carry the same {@code eventId}.
     * </p>
     *
     * @param handlerFactory Factory for constructing a {@link NetworkingGameClientHandler}.
     * @param ip             The IP address of the server to connect to.
     * @param port           The port number of the server to connect to.
     * @param eventId        A unique identifier for this event, typically injected
     *                       automatically by the {@link org.toop.framework.eventbus.EventFlow}.
     */
    public record StartClient(
            Supplier<? extends NetworkingGameClientHandler> handlerFactory,
            String ip,
            int port,
            String eventId
    ) implements EventWithUuid {

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
        public String eventId() {
            return this.eventId;
        }
    }

    /**
     * TODO: Update docs new input.
     * BLOCKING Triggers starting a server connection and returns a future.
     *
     * @param ip The IP address of the server to connect to.
     * @param port The port of the server to connect to.
     * @param future Returns the UUID of the connection, when connection is established.
     */
    public record StartClientRequest(
            Supplier<? extends NetworkingGameClientHandler> handlerFactory,
            String ip, int port, CompletableFuture<String> future) implements EventWithoutUuid {}

    /**
     *
     * @param clientId The ID of the client to be used in requests.
     * @param eventId The eventID used in checking if event is for you.
     */
    public record StartClientSuccess(String clientId, String eventId)
            implements EventWithUuid {
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
        public String eventId() {
            return this.eventId;
        }
    }

    /**
     * Triggers sending a command to a server.
     *
     * @param connectionId The UUID of the connection to send the command on.
     * @param args The command arguments.
     */
    public record SendCommand(String connectionId, String... args) implements EventWithoutUuid {}
    /**
     * Triggers reconnecting to a previous address.
     *
     * @param connectionId The identifier of the connection being reconnected.
     */
    public record Reconnect(Object connectionId) implements EventWithoutUuid {}


    /**
     * Triggers when the server client receives a message.
     *
     * @param ConnectionUuid The UUID of the connection that received the message.
     * @param message The message received.
     */
    public record ReceivedMessage(String ConnectionUuid, String message) implements EventWithoutUuid {}

    /**
     * Triggers changing connection to a new address.
     *
     * @param connectionId The identifier of the connection being changed.
     * @param ip The new IP address.
     * @param port The new port.
     */
    public record ChangeClient(Object connectionId, String ip, int port) implements EventWithoutUuid {}


    /**
     * Triggers when the server couldn't connect to the desired address.
     *
     * @param connectionId The identifier of the connection that failed.
     */
    public record CouldNotConnect(Object connectionId) implements EventWithoutUuid {}

    /** WIP Triggers when a connection closes. */
    public record ClosedConnection() implements EventWithoutUuid {}

}
