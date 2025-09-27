package org.toop.framework.eventbus;

import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.events.EventWithSnowflake;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * EventFlow is a utility class for creating, posting, and optionally subscribing to events
 * in a type-safe and chainable manner. It is designed to work with the {@link GlobalEventBus}.
 *
 * <p>This class supports automatic UUID assignment for {@link EventWithSnowflake} events,
 * and allows filtering subscribers so they only respond to events with a specific UUID.
 * All subscription methods are chainable, and you can configure automatic unsubscription
 * after an event has been successfully handled.</p>
 */
public class EventFlow {



    /** Lookup object used for dynamically invoking constructors via MethodHandles. */
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /** Cache of constructor handles for event classes to avoid repeated reflection lookups. */
    private static final Map<Class<?>, MethodHandle> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

    /** Automatically assigned UUID for {@link EventWithSnowflake} events. */
    private long eventSnowflake = -1;

    /** The event instance created by this publisher. */
    private EventType event = null;

    /** The listener returned by GlobalEventBus subscription. Used for unsubscription. */
    private final List<ListenerHandler> listeners = new ArrayList<>();

    /** Holds the results returned from the subscribed event, if any. */
    private Map<String, Object> result = null;

    /** Empty constructor (event must be added via {@link #addPostEvent(Class, Object...)}). */
    public EventFlow() {}

    // New: accept an event instance directly
    public EventFlow addPostEvent(EventType event) {
        this.event = event;
        return this;
    }

    // Optional: accept a Supplier<EventType> to defer construction
    public EventFlow addPostEvent(Supplier<? extends EventType> eventSupplier) {
        this.event = eventSupplier.get();
        return this;
    }

    // Keep the old class+args version if needed
    public <T extends EventType> EventFlow addPostEvent(Class<T> eventClass, Object... args) {
        try {
            boolean isUuidEvent = EventWithSnowflake.class.isAssignableFrom(eventClass);

            MethodHandle ctorHandle = CONSTRUCTOR_CACHE.computeIfAbsent(eventClass, cls -> {
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
                this.eventSnowflake = new SnowflakeGenerator().nextId();
                finalArgs = new Object[args.length + 1];
                System.arraycopy(args, 0, finalArgs, 0, args.length);
                finalArgs[args.length] = this.eventSnowflake;
            } else if (isUuidEvent) {
                this.eventSnowflake = (Long) args[args.length - 1];
                finalArgs = args;
            } else {
                finalArgs = args;
            }

            this.event = (EventType) ctorHandle.invokeWithArguments(finalArgs);
            return this;

        } catch (Throwable e) {
            throw new RuntimeException("Failed to instantiate event", e);
        }
    }

//    public EventFlow addSnowflake() {
//        this.eventSnowflake = new SnowflakeGenerator(1).nextId();
//        return this;
//    }

    /**
     * Subscribe by ID: only fires if UUID matches this publisher's eventId.
     */
    public <TT extends EventWithSnowflake> EventFlow onResponse(Class<TT> eventClass, Consumer<TT> action,
                                                                boolean unsubscribeAfterSuccess) {
        ListenerHandler[] listenerHolder = new ListenerHandler[1];
        listenerHolder[0] = new ListenerHandler(
                GlobalEventBus.subscribe(eventClass, event -> {
                    if (event.eventSnowflake() != this.eventSnowflake) return;

                    action.accept(event);

                    if (unsubscribeAfterSuccess && listenerHolder[0] != null) {
                        GlobalEventBus.unsubscribe(listenerHolder[0]);
                        this.listeners.remove(listenerHolder[0]);
                    }

                    this.result = event.result();
                })
        );
        this.listeners.add(listenerHolder[0]);
        return this;
    }

    /**
     * Subscribe by ID: only fires if UUID matches this publisher's eventId.
     */
    public <TT extends EventWithSnowflake> EventFlow onResponse(Class<TT> eventClass, Consumer<TT> action) {
        return this.onResponse(eventClass, action, true);
    }

    /**
     * Subscribe by ID without explicit class.
     */
    @SuppressWarnings("unchecked")
    public <TT extends EventWithSnowflake> EventFlow onResponse(Consumer<TT> action, boolean unsubscribeAfterSuccess) {
        ListenerHandler[] listenerHolder = new ListenerHandler[1];
        listenerHolder[0] = new ListenerHandler(
            GlobalEventBus.subscribe(event -> {
                if (!(event instanceof EventWithSnowflake uuidEvent)) return;
                if (uuidEvent.eventSnowflake() == this.eventSnowflake) {
                    try {
                        TT typedEvent = (TT) uuidEvent;
                        action.accept(typedEvent);
                        if (unsubscribeAfterSuccess && listenerHolder[0] != null) {
                            GlobalEventBus.unsubscribe(listenerHolder[0]);
                            this.listeners.remove(listenerHolder[0]);
                        }
                        this.result = typedEvent.result();
                    } catch (ClassCastException _) {
                        throw new ClassCastException("Cannot cast " + event.getClass().getName() +
                                " to EventWithSnowflake");
                    }
                }
            })
        );
        this.listeners.add(listenerHolder[0]);
        return this;
    }

    public <TT extends EventWithSnowflake> EventFlow onResponse(Consumer<TT> action) {
        return this.onResponse(action, true);
    }

    public <TT extends EventType> EventFlow listen(Class<TT> eventClass, Consumer<TT> action,
                                                               boolean unsubscribeAfterSuccess) {
        ListenerHandler[] listenerHolder = new ListenerHandler[1];
        listenerHolder[0] = new ListenerHandler(
                GlobalEventBus.subscribe(eventClass, event -> {
                    action.accept(event);

                    if (unsubscribeAfterSuccess && listenerHolder[0] != null) {
                        GlobalEventBus.unsubscribe(listenerHolder[0]);
                        this.listeners.remove(listenerHolder[0]);
                    }
                })
        );
        this.listeners.add(listenerHolder[0]);
        return this;
    }

    public <TT extends EventType> EventFlow listen(Class<TT> eventClass, Consumer<TT> action) {
        return this.listen(eventClass, action, true);
    }

    @SuppressWarnings("unchecked")
    public <TT extends EventType> EventFlow listen(Consumer<TT> action, boolean unsubscribeAfterSuccess) {
        ListenerHandler[] listenerHolder = new ListenerHandler[1];
        listenerHolder[0] = new ListenerHandler(
                GlobalEventBus.subscribe(event -> {
                    if (!(event instanceof EventType nonUuidEvent)) return;
                    try {
                        TT typedEvent = (TT) nonUuidEvent;
                        action.accept(typedEvent);
                        if (unsubscribeAfterSuccess && listenerHolder[0] != null) {
                            GlobalEventBus.unsubscribe(listenerHolder[0]);
                            this.listeners.remove(listenerHolder[0]);
                        }
                    } catch (ClassCastException _) {
                        throw new ClassCastException("Cannot cast " + event.getClass().getName() +
                                " to EventWithSnowflake");
                    }
                })
        );
        this.listeners.add(listenerHolder[0]);
        return this;
    }

    public <TT extends EventType> EventFlow listen(Consumer<TT> action) {
        return this.listen(action, true);
    }

    /** Post synchronously */
    public EventFlow postEvent() {
        GlobalEventBus.post(this.event);
        return this;
    }

    /** Post asynchronously */
    public EventFlow asyncPostEvent() {
        GlobalEventBus.postAsync(this.event);
        return this;
    }

    public Map<String, Object> getResult() {
        return this.result;
    }

    public EventType getEvent() {
        return event;
    }

    public ListenerHandler[] getListeners() {
        return listeners.toArray(new ListenerHandler[0]);
    }

    public long getEventSnowflake() {
        return eventSnowflake;
    }
}
