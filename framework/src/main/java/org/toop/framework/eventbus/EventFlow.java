package org.toop.framework.eventbus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.events.ResponseToUniqueEvent;
import org.toop.framework.eventbus.events.UniqueEvent;

/**
 * EventFlow is a utility class for creating, posting, and optionally subscribing to events in a
 * type-safe and chainable manner. It is designed to work with the {@link GlobalEventBus}.
 *
 * <p>This class supports automatic UUID assignment for {@link UniqueEvent} events, and
 * allows filtering subscribers so they only respond to events with a specific UUID. All
 * subscription methods are chainable, and you can configure automatic unsubscription after an event
 * has been successfully handled.
 */
public class EventFlow {

    /** Lookup object used for dynamically invoking constructors via MethodHandles. */
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /** Cache of constructor handles for event classes to avoid repeated reflection lookups. */
    private static final Map<Class<?>, MethodHandle> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

    /** Automatically assigned UUID for {@link UniqueEvent} events. */
    private long eventSnowflake = -1;

    /** The event instance created by this publisher. */
    private EventType event = null;

    /** The listener returned by GlobalEventBus subscription. Used for unsubscription. */
    private final List<ListenerHandler<?>> listeners = new ArrayList<>();

    /** Holds the results returned from the subscribed event, if any. */
    private Map<String, ?> result = null;

    /** Empty constructor (event must be added via {@link #addPostEvent(Class, Object...)}). */
    public EventFlow() {}

    /**
     *
     * Add an event that will be triggered when {@link #postEvent()} or {@link #asyncPostEvent()} is called.
     *
     * @param eventClass The event that will be posted.
     * @param args The event arguments, see the added event record for more information.
     * @return {@link #EventFlow}
     *
     */
    public <T extends EventType> EventFlow addPostEvent(Class<T> eventClass, Object... args) {
        try {
            boolean isUuidEvent = UniqueEvent.class.isAssignableFrom(eventClass);

            MethodHandle ctorHandle =
                    CONSTRUCTOR_CACHE.computeIfAbsent(
                            eventClass,
                            cls -> {
                                try {
                                    Class<?>[] paramTypes =
                                            cls.getDeclaredConstructors()[0].getParameterTypes();
                                    MethodType mt = MethodType.methodType(void.class, paramTypes);
                                    return LOOKUP.findConstructor(cls, mt);
                                } catch (Exception e) {
                                    throw new RuntimeException(
                                            "Failed to find constructor handle for " + cls, e);
                                }
                            });

            Object[] finalArgs;
            int expectedParamCount = ctorHandle.type().parameterCount();

            if (isUuidEvent && args.length < expectedParamCount) {
                this.eventSnowflake = SnowflakeGenerator.nextId();
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

    /**
     *
     * Add an event that will be triggered when {@link #postEvent()} or {@link #asyncPostEvent()} is called.
     *
     * @param event The event to be posted.
     * @return {@link #EventFlow}
     *
     */
    public EventFlow addPostEvent(EventType event) {
        this.event = event;
        return this;
    }

    /**
     *
     * Add an event that will be triggered when {@link #postEvent()} or {@link #asyncPostEvent()} is called.
     *
     * @param eventSupplier The event that will be posted through a Supplier.
     * @return {@link #EventFlow}
     *
     */
    public EventFlow addPostEvent(Supplier<? extends EventType> eventSupplier) {
        this.event = eventSupplier.get();
        return this;
    }

    /**
     *
     * Start listening for an event and trigger when ID correlates.
     *
     * @param event The {@link ResponseToUniqueEvent} to trigger the lambda.
     * @param action The lambda to run when triggered.
     * @param unsubscribeAfterSuccess Enable/disable auto unsubscribing to event after being triggered.
     * @param name A name given to the event, can later be used to unsubscribe.
     * @return {@link #EventFlow}
     *
     */
    public <TT extends ResponseToUniqueEvent> EventFlow onResponse(
            Class<TT> event, Consumer<TT> action, boolean unsubscribeAfterSuccess, String name
    ) {

        final long id = SnowflakeGenerator.nextId();

        Consumer<TT> newAction = eventClass -> {
            if (eventClass.getIdentifier() != this.eventSnowflake) return;

            action.accept(eventClass);

            if (unsubscribeAfterSuccess) unsubscribe(id);

            this.result = eventClass.result();
        };

        // TODO Remove casts
        var listener = new ListenerHandler<>(
                id,
                name,
                (Class<ResponseToUniqueEvent>) event,
                (Consumer<ResponseToUniqueEvent>) newAction
        );

        GlobalEventBus.get().subscribe(listener);
        this.listeners.add(listener);
        return this;
    }

    /**
     *
     * Start listening for an event and trigger when ID correlates, auto unsubscribes after being triggered and adds an empty name.
     *
     * @param event The {@link ResponseToUniqueEvent} to trigger the lambda.
     * @param action The lambda to run when triggered.
     * @return {@link #EventFlow}
     *
     */
    public <TT extends ResponseToUniqueEvent> EventFlow onResponse(Class<TT> event, Consumer<TT> action) {
        return this.onResponse(event, action, true, "");
    }

    /**
     *
     * Start listening for an event and trigger when ID correlates, auto adds an empty name.
     *
     * @param event The {@link ResponseToUniqueEvent} to trigger the lambda.
     * @param action The lambda to run when triggered.
     * @param unsubscribeAfterSuccess Enable/disable auto unsubscribing to event after being triggered.
     * @return {@link #EventFlow}
     *
     */
    public <TT extends ResponseToUniqueEvent> EventFlow onResponse(Class<TT> event, Consumer<TT> action, boolean unsubscribeAfterSuccess) {
        return this.onResponse(event, action, unsubscribeAfterSuccess, "");
    }

    /**
     *
     * Start listening for an event and trigger when ID correlates, auto unsubscribes after being triggered.
     *
     * @param event The {@link ResponseToUniqueEvent} to trigger the lambda.
     * @param action The lambda to run when triggered.
     * @param name A name given to the event, can later be used to unsubscribe.
     * @return {@link #EventFlow}
     *
     */
    public <TT extends ResponseToUniqueEvent> EventFlow onResponse(Class<TT> event, Consumer<TT> action, String name) {
        return this.onResponse(event, action, true, name);
    }

    /**
     *
     * Subscribe by ID without explicit class.
     *
     * @param action The lambda to run when triggered.
     * @return {@link #EventFlow}
     *
     * @deprecated use {@link #onResponse(Class, Consumer, boolean, String)} instead.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <TT extends ResponseToUniqueEvent> EventFlow onResponse(
            Consumer<TT> action, boolean unsubscribeAfterSuccess, String name) {

        final long id = SnowflakeGenerator.nextId();

        Consumer<TT> newAction = event -> {
            if (!(event instanceof UniqueEvent uuidEvent)) return;
            if (uuidEvent.getIdentifier() == this.eventSnowflake) {
                try {
                    TT typedEvent = (TT) uuidEvent;
                    action.accept(typedEvent);

                    if (unsubscribeAfterSuccess) unsubscribe(id);

                    this.result = typedEvent.result();
                } catch (ClassCastException _) {
                    throw new ClassCastException(
                            "Cannot cast "
                                    + event.getClass().getName()
                                    + " to UniqueEvent");
                }
            }
        };

        var listener = new ListenerHandler<>(
                id,
                name,
                (Class<TT>) action.getClass().getDeclaredMethods()[0].getParameterTypes()[0],
                newAction
        );

        GlobalEventBus.get().subscribe(listener);
        this.listeners.add(listener);
        return this;
    }

    /**
     *
     * Subscribe by ID without explicit class.
     *
     * @param action The lambda to run when triggered.
     * @return {@link #EventFlow}
     *
     * @deprecated use {@link #onResponse(Class, Consumer)} instead.
     */
    @Deprecated
    public <TT extends ResponseToUniqueEvent> EventFlow onResponse(Consumer<TT> action) {
        return this.onResponse(action, true, "");
    }

    /**
     *
     * Start listening for an event, and run a lambda when triggered.
     *
     * @param event The {@link EventType} to trigger the lambda.
     * @param action The lambda to run when triggered.
     * @param unsubscribeAfterSuccess Enable/disable auto unsubscribing to event after being triggered.
     * @param name A name given to the event, can later be used to unsubscribe.
     * @return {@link #EventFlow}
     *
     */
    public <TT extends EventType> EventFlow listen(
            Class<TT> event, Consumer<TT> action, boolean unsubscribeAfterSuccess, String name) {

        long id = SnowflakeGenerator.nextId();

        Consumer<TT> newAction = eventc -> {
            action.accept(eventc);

            if (unsubscribeAfterSuccess) unsubscribe(id);
        };

        var listener = new ListenerHandler<>(
                        id,
                        name,
                        event,
                        newAction
                );

        GlobalEventBus.get().subscribe(listener);
        this.listeners.add(listener);
        return this;
    }

    /**
     *
     * Start listening for an event, and run a lambda when triggered, auto unsubscribes.
     *
     * @param event The {@link EventType} to trigger the lambda.
     * @param action The lambda to run when triggered.
     * @param name A name given to the event, can later be used to unsubscribe.
     * @return {@link #EventFlow}
     *
     */
    public <TT extends EventType> EventFlow listen(Class<TT> event, Consumer<TT> action, String name) {
        return this.listen(event, action, true, name);
    }

    /**
     *
     * Start listening for an event, and run a lambda when triggered, auto unsubscribe and gives it an empty name.
     *
     * @param event The {@link EventType} to trigger the lambda.
     * @param action The lambda to run when triggered.
     * @return {@link #EventFlow}
     *
     */
    public <TT extends EventType> EventFlow listen(Class<TT> event, Consumer<TT> action) {
        return this.listen(event, action, true, "");
    }

    /**
     *
     * Start listening for an event, and run a lambda when triggered, adds an empty name.
     *
     * @param event The {@link EventType} to trigger the lambda.
     * @param action The lambda to run when triggered.
     * @param unsubscribeAfterSuccess Enable/disable auto unsubscribing to event after being triggered.
     * @return {@link #EventFlow}
     *
     */
    public <TT extends EventType> EventFlow listen(Class<TT> event, Consumer<TT> action, boolean unsubscribeAfterSuccess) {
        return this.listen(event, action, unsubscribeAfterSuccess, "");
    }

    /**
     *
     * Start listening to an event.
     *
     * @param action The lambda to run when triggered.
     * @return {@link EventFlow}
     *
     * @deprecated use {@link #listen(Class, Consumer, boolean, String)} instead.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <TT extends EventType> EventFlow listen(
            Consumer<TT> action, boolean unsubscribeAfterSuccess, String name) {
        long id = SnowflakeGenerator.nextId();

        Class<TT> eventClass = (Class<TT>) action.getClass().getDeclaredMethods()[0].getParameterTypes()[0];

        Consumer<TT> newAction = event -> {
            if (!(event instanceof EventType nonUuidEvent)) return;
            try {
                TT typedEvent = (TT) nonUuidEvent;
                action.accept(typedEvent);
                if (unsubscribeAfterSuccess) unsubscribe(id);
            } catch (ClassCastException _) {
                throw new ClassCastException(
                        "Cannot cast "
                                + event.getClass().getName()
                                + " to UniqueEvent");
            }
        };

        var listener = new ListenerHandler<>(
                id,
                name,
                eventClass,
                newAction
        );

        GlobalEventBus.get().subscribe(listener);
        this.listeners.add(listener);
        return this;
    }

    /**
     *
     * Start listening to an event.
     *
     * @param action The lambda to run when triggered.
     * @return {@link EventFlow}
     *
     * @deprecated use {@link #listen(Class, Consumer)} instead.
     */
    @Deprecated
    public <TT extends EventType> EventFlow listen(Consumer<TT> action) {
        return this.listen(action, true, "");
    }

    /**
     * Posts the event added through {@link #addPostEvent}.
     */
    public EventFlow postEvent() {
        GlobalEventBus.get().post(this.event);
        return this;
    }

    /**
     * Posts the event added through {@link #addPostEvent} asynchronously.
     *
     * @deprecated use {@link #postEvent()} instead.
     */
    @Deprecated
    public EventFlow asyncPostEvent() {
        GlobalEventBus.get().post(this.event);
        return this;
    }

    /**
     *
     * Unsubscribe from an event.
     *
     * @param listenerObject The listener object to remove and unsubscribe.
     */
    public void unsubscribe(Object listenerObject) {
        this.listeners.removeIf(handler -> {
            if (handler.getListener() == listenerObject) {
                GlobalEventBus.get().unsubscribe(handler);
                return true;
            }
            return false;
        });
    }

    /**
     *
     * Unsubscribe from an event.
     *
     * @param listenerId The id given to the {@link ListenerHandler}.
     */
    public void unsubscribe(long listenerId) {
        this.listeners.removeIf(handler -> {
            if (handler.getId() == listenerId) {
                GlobalEventBus.get().unsubscribe(handler);
                return true;
            }
            return false;
        });
    }

    /**
     * Unsubscribe from an event.
     *
     * @param name The name given to the listener.
     */
    public void unsubscribe(String name) {
        this.listeners.removeIf(handler -> {
            if (handler.getName().equals(name)) {
                GlobalEventBus.get().unsubscribe(handler);
                return true;
            }
            return false;
        });
    }

    /**
     * Unsubscribe all events.
     */
    public void unsubscribeAll() {
        listeners.removeIf(handler -> {
            GlobalEventBus.get().unsubscribe(handler);
            return true;
        });
    }

    /**
     * Clean and remove everything inside {@link EventFlow}.
     */
    private void clean() {
        unsubscribeAll();
        this.event = null;
        this.result = null;
    } // TODO

    /**
     *  TODO
     *
     * @return TODO
     */
    public Map<String, ?> getResult() {
        return this.result;
    }

    /**
     *  TODO
     *
     * @return TODO
     */
    public EventType getEvent() {
        return event;
    }

    /**
     *
     * Returns a copy of the list of listeners.
     *
     * @return Copy of the list of listeners.
     */
    public ListenerHandler[] getListeners() {
        return listeners.toArray(new ListenerHandler[0]);
    }

    /**
     *  Returns the generated snowflake for the {@link EventFlow}
     *
     * @return The generated snowflake for this {@link EventFlow}
     */
    public long getEventSnowflake() {
        return eventSnowflake;
    }
}
