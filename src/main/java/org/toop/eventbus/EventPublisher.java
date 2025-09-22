package org.toop.eventbus;

import com.google.common.eventbus.EventBus;
import org.toop.eventbus.events.EventWithUuid;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * EventPublisher is a helper class for creating, posting, and optionally subscribing to events
 * in a type-safe and chainable manner. It automatically injects a unique UUID into the event
 * and supports filtering subscribers by this UUID.
 *
 * <p>Usage pattern (with chainable API):
 * <pre>{@code
 * new EventPublisher<>(StartClient.class, handlerFactory, "127.0.0.1", 5001)
 *     .onEventById(ClientReady.class, clientReadyEvent -> logger.info(clientReadyEvent))
 *     .unregisterAfterSuccess()
 *     .postEvent();
 * }</pre>
 *
 * @param <T> the type of event to publish, must extend EventWithUuid
 */
public class EventPublisher<T extends EventWithUuid> {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<?>, MethodHandle> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

    /** The UUID automatically assigned to this event */
    private final String eventId;

    /** The event instance created by this publisher */
    private final T event;

    /** The listener object returned by the global event bus subscription */
    private Object listener;

    /** Flag indicating whether to unregister the listener after it is successfully triggered */
    private boolean unregisterAfterSuccess = false;

    /** Results that came back from the subscribed event */
    private Map<String, Object> result = null;

    /**
     * Constructs a new EventPublisher by instantiating the given event class.
     * A unique UUID is automatically generated and passed as the last constructor argument.
     *
     * @param postEventClass the class of the event to instantiate
     * @param args           constructor arguments for the event, excluding the UUID
     * @throws RuntimeException if instantiation fails
     */
    public EventPublisher(Class<T> postEventClass, Object... args) {
        this.eventId = UUID.randomUUID().toString();

        try {
            MethodHandle ctorHandle = CONSTRUCTOR_CACHE.computeIfAbsent(postEventClass, cls -> {
                try {
                    // Build signature dynamically (arg types + String for UUID)
                    Class<?>[] paramTypes = cls.getDeclaredConstructors()[0].getParameterTypes();
                    MethodType mt = MethodType.methodType(void.class, paramTypes);
                    return LOOKUP.findConstructor(cls, mt);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to find constructor handle for " + cls, e);
                }
            });

            // Append UUID to args
            Object[] finalArgs = new Object[args.length + 1];
            System.arraycopy(args, 0, finalArgs, 0, args.length);
            finalArgs[args.length] = this.eventId;
            // --------------------

            @SuppressWarnings("unchecked")
            T instance = (T) ctorHandle.invokeWithArguments(finalArgs);
            this.event = instance;

        } catch (Throwable e) {
            throw new RuntimeException("Failed to instantiate event", e);
        }
    }

    public EventPublisher(EventBus eventbus, Class<T> postEventClass, Object... args) {
        this.eventId = UUID.randomUUID().toString();

        try {
            MethodHandle ctorHandle = CONSTRUCTOR_CACHE.computeIfAbsent(postEventClass, cls -> {
                try {
                    // Build signature dynamically (arg types + String for UUID)
                    Class<?>[] paramTypes = cls.getDeclaredConstructors()[0].getParameterTypes();
                    MethodType mt = MethodType.methodType(void.class, paramTypes);
                    return LOOKUP.findConstructor(cls, mt);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to find constructor handle for " + cls, e);
                }
            });

            // Append UUID to args
            Object[] finalArgs = new Object[args.length + 1];
            System.arraycopy(args, 0, finalArgs, 0, args.length);
            finalArgs[args.length] = this.eventId;
            // --------------------

            @SuppressWarnings("unchecked")
            T instance = (T) ctorHandle.invokeWithArguments(finalArgs);
            this.event = instance;

        } catch (Throwable e) {
            throw new RuntimeException("Failed to instantiate event", e);
        }
    }

    /**
     * Subscribes a listener for a specific event type, but only triggers the listener
     * if the incoming event's UUID matches this EventPublisher's UUID.
     *
     * @param eventClass the class of the event to subscribe to
     * @param action     the action to execute when a matching event is received
     * @param <TT>       the type of the event to subscribe to; must extend EventWithUuid
     * @return this EventPublisher instance, for chainable calls
     */
    public <TT extends EventWithUuid> EventPublisher<T> onEventById(
            Class<TT> eventClass, Consumer<TT> action) {

        this.listener = GlobalEventBus.subscribeAndRegister(eventClass, event -> {
            if (event.eventId().equals(this.eventId)) {
                action.accept(event);

                if (unregisterAfterSuccess && listener != null) {
                    GlobalEventBus.unregister(listener);
                }

                this.result = event.result();
            }
        });

        return this;
    }

    /**
     * Posts the event to the global event bus. This should generally be the
     * final call in the chain.
     *
     * @return this EventPublisher instance, for potential chaining
     */
    public EventPublisher<T> postEvent() {
        GlobalEventBus.post(event);
        return this;
    }

    /**
     * Configures the publisher so that any listener registered with
     * {@link #onEventById(Class, Consumer)} is automatically unregistered
     * after it is successfully triggered.
     *
     * @return this EventPublisher instance, for chainable calls
     */
    public EventPublisher<T> unregisterAfterSuccess() {
        this.unregisterAfterSuccess = true;
        return this;
    }

    public Map<String, Object> getResult() {
        if (this.result != null) {
            return this.result;
        }
        return null;
        // TODO: Why check for null if return is null anyway?
    }

    /**
     * Returns the event instance created by this publisher.
     *
     * @return the event instance
     */
    public T getEvent() {
        return event;
    }

    /**
     * Returns the UUID automatically assigned to this event.
     *
     * @return the UUID of the event
     */
    public String getEventId() {
        return eventId;
    }
}