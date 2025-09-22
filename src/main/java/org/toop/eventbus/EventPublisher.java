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
public class EventPublisher<T> {

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
            boolean isUuidEvent = EventWithUuid.class.isAssignableFrom(postEventClass);

            MethodHandle ctorHandle = CONSTRUCTOR_CACHE.computeIfAbsent(postEventClass, cls -> {
                try {
                    Class<?>[] paramTypes = cls.getDeclaredConstructors()[0].getParameterTypes();
                    MethodType mt = MethodType.methodType(void.class, paramTypes);
                    return LOOKUP.findConstructor(cls, mt);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to find constructor handle for " + cls, e);
                }
            });

            Object[] finalArgs;
            if (isUuidEvent) {
                // append UUID to args
                finalArgs = new Object[args.length + 1];
                System.arraycopy(args, 0, finalArgs, 0, args.length);
                finalArgs[args.length] = this.eventId;
            } else {
                // just forward args
                finalArgs = args;
            }

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
     * Subscribes a listener for a specific event type, but only triggers the listener
     * if the incoming event's UUID matches this EventPublisher's UUID.
     *
     * @param action     the action (function) to execute when a matching event is received
     * @param <TT>       the type of the event to subscribe to; must extend EventWithUuid
     * @return this EventPublisher instance, for chainable calls
     */
    @SuppressWarnings("unchecked")
    public <TT extends EventWithUuid> EventPublisher<T> onEventById(
            Consumer<TT> action) {

        this.listener = GlobalEventBus.subscribeAndRegister(event -> {
            // Only process events that are EventWithUuid
            if (event instanceof EventWithUuid uuidEvent) {
                if (uuidEvent.eventId().equals(this.eventId)) {
                    try {
                        TT typedEvent = (TT) uuidEvent; // unchecked cast
                        action.accept(typedEvent);

                        if (unregisterAfterSuccess && listener != null) {
                            GlobalEventBus.unregister(listener);
                        }

                        this.result = typedEvent.result();
                    } catch (ClassCastException ignored) {
                        // TODO: Not the right type, ignore silently
                    }
                }
            }
        });

        return this;
    }

    /**
     * Subscribes a listener for a specific event type. The listener will be invoked
     * whenever an event of the given class is posted to the global event bus.
     *
     * <p>This overload provides type safety by requiring the event class explicitly
     * and casting the incoming event before passing it to the provided action.</p>
     *
     * <pre>{@code
     * new EventPublisher<>(MyEvent.class)
     *     .onEvent(MyEvent.class, e -> logger.info("Received: " + e))
     *     .postEvent();
     * }</pre>
     *
     * @param eventClass the class of the event to subscribe to
     * @param action     the action to execute when an event of the given class is received
     * @param <TT>       the type of the event to subscribe to
     * @return this EventPublisher instance, for chainable calls
     */
    public <TT> EventPublisher<T> onEvent(Class<TT> eventClass, Consumer<TT> action) {
        this.listener = GlobalEventBus.subscribeAndRegister(eventClass, event -> {
            action.accept(eventClass.cast(event));

            if (unregisterAfterSuccess && listener != null) {
                GlobalEventBus.unregister(listener);
            }
        });
        return this;
    }

    /**
     * Subscribes a listener for events without requiring the event class explicitly.
     * The listener will attempt to cast each posted event to the expected type.
     * If the cast fails, the event is ignored silently.
     *
     * <p>This overload provides more concise syntax, but relies on an unchecked cast
     * at runtime. Use {@link #onEvent(Class, Consumer)} if you prefer explicit
     * type safety.</p>
     *
     * <pre>{@code
     * new EventPublisher<>(MyEvent.class)
     *     .onEvent((MyEvent e) -> logger.info("Received: " + e))
     *     .postEvent();
     * }</pre>
     *
     * @param action the action to execute when a matching event is received
     * @param <TT>   the type of the event to subscribe to
     * @return this EventPublisher instance, for chainable calls
     */
    @SuppressWarnings("unchecked")
    public <TT> EventPublisher<T> onEvent(Consumer<TT> action) {
        this.listener = GlobalEventBus.subscribeAndRegister(event -> {
            try {
                // unchecked cast – if wrong type, ClassCastException is caught
                TT typedEvent = (TT) event;
                action.accept(typedEvent);

                if (unregisterAfterSuccess && listener != null) {
                    GlobalEventBus.unregister(listener);
                }
            } catch (ClassCastException ignored) {
                // Ignore events of unrelated types
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

    public EventPublisher<T> unregisterNow() {
        if (unregisterAfterSuccess && listener != null) {
            GlobalEventBus.unregister(listener);
        }
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