package org.toop.eventbus;

import org.toop.eventbus.events.EventWithUuid;
import org.toop.eventbus.events.IEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * EventPublisher is a utility class for creating, posting, and optionally subscribing to events
 * in a type-safe and chainable manner. It is designed to work with the {@link GlobalEventBus}.
 *
 * <p>This class supports automatic UUID assignment for {@link EventWithUuid} events,
 * and allows filtering subscribers so they only respond to events with a specific UUID.
 * All subscription methods are chainable, and you can configure automatic unsubscription
 * after an event has been successfully handled.</p>
 *
 * <p><strong>Usage patterns:</strong></p>
 *
 * <p><strong>1. Publish an event with optional subscription by UUID:</strong></p>
 * <pre>{@code
 * new EventPublisher<>(StartClient.class, handlerFactory, "127.0.0.1", 5001)
 *     .onEventById(ClientReady.class, clientReadyEvent -> logger.info(clientReadyEvent))
 *     .unsubscribeAfterSuccess()
 *     .postEvent();
 * }</pre>
 *
 * <p><strong>2. Subscribe to a specific event type without UUID filtering:</strong></p>
 * <pre>{@code
 * new EventPublisher<>(MyEvent.class)
 *     .onEvent(MyEvent.class, e -> logger.info("Received: " + e))
 *     .postEvent();
 * }</pre>
 *
 * <p><strong>3. Subscribe with runtime type inference:</strong></p>
 * <pre>{@code
 * new EventPublisher<>((MyEvent e) -> logger.info("Received: " + e))
 *     .postEvent();
 * }</pre>
 *
 * <p><strong>Notes:</strong></p>
 * <ul>
 *     <li>For events extending {@link EventWithUuid}, a UUID is automatically generated
 *         and passed to the event constructor if none is provided.</li>
 *     <li>Listeners registered via {@code onEventById} will only be triggered
 *         if the event's UUID matches this publisher's UUID.</li>
 *     <li>Listeners can be unsubscribed automatically after the first successful trigger
 *         using {@link #unsubscribeAfterSuccess()}.</li>
 *     <li>All subscription and posting methods are chainable for fluent API usage.</li>
 * </ul>
 *
 * @param <T> the type of event to publish; must implement {@link IEvent}
 */
public class EventPublisher<T extends IEvent> {


    /** Lookup object used for dynamically invoking constructors via MethodHandles. */
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /** Cache of constructor handles for event classes to avoid repeated reflection lookups. */
    private static final Map<Class<?>, MethodHandle> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

    /** Automatically assigned UUID for {@link EventWithUuid} events. */
    private String eventId = null;

    /** The event instance created by this publisher. */
    private T event = null;

    /** The listener returned by GlobalEventBus subscription. Used for unsubscription. */
    private Object listener;

    /** Flag indicating whether to automatically unsubscribe the listener after success. */
    private boolean unsubscribeAfterSuccess = false;

    /** Holds the results returned from the subscribed event, if any. */
    private Map<String, Object> result = null;

    /**
     * Constructs a new EventPublisher by instantiating the given event class.
     * For {@link EventWithUuid} events, a UUID is automatically generated and passed as
     * the last constructor argument if not explicitly provided.
     *
     * @param postEventClass the class of the event to instantiate
     * @param args      constructor arguments for the event (UUID may be excluded)
     * @throws RuntimeException if instantiation fails
     */
    public EventPublisher(Class<T> postEventClass, Object... args) {
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
            int expectedParamCount = ctorHandle.type().parameterCount();
            if (isUuidEvent && args.length < expectedParamCount) {
                this.eventId = UUID.randomUUID().toString();
                finalArgs = new Object[args.length + 1];
                System.arraycopy(args, 0, finalArgs, 0, args.length);
                finalArgs[args.length] = this.eventId;
            } else if (isUuidEvent) {
                this.eventId = (String) args[args.length - 1];
                finalArgs = args;
            } else {
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
     * Creates a new EventPublisher and immediately subscribes a listener for the event class.
     *
     * @param eventClass the class of the event to subscribe to
     * @param action     the action to execute when an event of the given class is received
     */
    public EventPublisher(Class<T> eventClass, Consumer<T> action) {
        this.onEvent(eventClass, action);
    }

    /**
     * Creates a new EventPublisher and immediately subscribes a listener using runtime type inference.
     * The event type is inferred at runtime. Wrong type casts are ignored silently.
     *
     * @param action the action to execute when a matching event is received
     */
    public EventPublisher(Consumer<T> action) {
        this.onEvent(action);
    }

    /**
     * Subscribes a listener for a specific {@link EventWithUuid} event type.
     * The listener is only triggered if the event UUID matches this publisher's UUID.
     *
     * @param eventClass the class of the event to subscribe to
     * @param action     the action to execute on a matching event
     * @param <TT>       type of event; must extend EventWithUuid
     * @return this EventPublisher for chainable calls
     */
    public <TT extends EventWithUuid> EventPublisher<T> onEventById(
            Class<TT> eventClass, Consumer<TT> action) {

        this.listener = GlobalEventBus.subscribe(eventClass, event -> {
            if (event.eventId().equals(this.eventId)) {
                action.accept(event);

                if (unsubscribeAfterSuccess && listener != null) {
                    GlobalEventBus.unsubscribe(listener);
                }

                this.result = event.result();
            }
        });

        return this;
    }

    /**
     * Subscribes a listener for {@link EventWithUuid} events without specifying class explicitly.
     * Only triggers for events whose UUID matches this publisher's UUID.
     *
     * @param action the action to execute on a matching event
     * @param <TT>   type of event; must extend EventWithUuid
     * @return this EventPublisher for chainable calls
     */
    @SuppressWarnings("unchecked")
    public <TT extends EventWithUuid> EventPublisher<T> onEventById(Consumer<TT> action) {

        this.listener = GlobalEventBus.subscribe(event -> {
            if (event instanceof EventWithUuid uuidEvent) {
                if (uuidEvent.eventId().equals(this.eventId)) {
                    try {
                        TT typedEvent = (TT) uuidEvent;
                        action.accept(typedEvent);

                        if (unsubscribeAfterSuccess && listener != null) {
                            GlobalEventBus.unsubscribe(listener);
                        }

                        this.result = typedEvent.result();
                    } catch (ClassCastException ignored) {}
                }
            }
        });

        return this;
    }

    /**
     * Subscribes a listener for a specific event type without UUID filtering.
     *
     * @param eventClass the class of the event to subscribe to
     * @param action     the action to execute on the event
     * @param <TT>       type of event; must implement IEvent
     * @return this EventPublisher for chainable calls
     */
    public <TT extends IEvent> EventPublisher<T> onEvent(Class<TT> eventClass, Consumer<TT> action) {
        this.listener = GlobalEventBus.subscribe(eventClass, event -> {
            action.accept(eventClass.cast(event));

            if (unsubscribeAfterSuccess && listener != null) {
                GlobalEventBus.unsubscribe(listener);
            }
        });
        return this;
    }

    /**
     * Subscribes a listener using runtime type inference. Wrong type casts are ignored silently.
     *
     * @param action the action to execute when a matching event is received
     * @param <TT>   type of event (inferred at runtime)
     * @return this EventPublisher for chainable calls
     */
    @SuppressWarnings("unchecked")
    public <TT> EventPublisher<T> onEvent(Consumer<TT> action) {
        this.listener = GlobalEventBus.subscribe(event -> {
            try {
                TT typedEvent = (TT) event;
                action.accept(typedEvent);

                if (unsubscribeAfterSuccess && listener != null) {
                    GlobalEventBus.unsubscribe(listener);
                }
            } catch (ClassCastException ignored) {}
        });
        return this;
    }

    /**
     * Posts the event synchronously to {@link GlobalEventBus}.
     *
     * @return this EventPublisher for chainable calls
     */
    public EventPublisher<T> postEvent() {
        GlobalEventBus.post(event);
        return this;
    }

    /**
     * Posts the event asynchronously to {@link GlobalEventBus}.
     *
     * @return this EventPublisher for chainable calls
     */
    public EventPublisher<T> asyncPostEvent() {
        GlobalEventBus.postAsync(event);
        return this;
    }

    /**
     * Configures automatic unsubscription for listeners registered via onEventById
     * after a successful trigger.
     *
     * @return this EventPublisher for chainable calls
     */
    public EventPublisher<T> unsubscribeAfterSuccess() {
        this.unsubscribeAfterSuccess = true;
        return this;
    }

    /**
     * Immediately unsubscribes the listener, if set.
     *
     * @return this EventPublisher for chainable calls
     */
    public EventPublisher<T> unsubscribeNow() {
        if (unsubscribeAfterSuccess && listener != null) {
            GlobalEventBus.unsubscribe(listener);
        }
        return this;
    }

    /**
     * Returns the results provided by the triggered event, if any.
     *
     * @return map of results, or null if none
     */
    public Map<String, Object> getResult() {
        return this.result;
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
     * Returns the automatically assigned UUID for {@link EventWithUuid} events.
     *
     * @return the UUID string, or null for non-UUID events
     */
    public String getEventId() {
        return eventId;
    }
}