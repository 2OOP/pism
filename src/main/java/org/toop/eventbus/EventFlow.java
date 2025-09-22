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
 * EventFlow is a utility class for creating, posting, and optionally subscribing to events
 * in a type-safe and chainable manner. It is designed to work with the {@link GlobalEventBus}.
 *
 * <p>This class supports automatic UUID assignment for {@link EventWithUuid} events,
 * and allows filtering subscribers so they only respond to events with a specific UUID.
 * All subscription methods are chainable, and you can configure automatic unsubscription
 * after an event has been successfully handled.</p>
 */
public class EventFlow {

    /** Lookup object used for dynamically invoking constructors via MethodHandles. */
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /** Cache of constructor handles for event classes to avoid repeated reflection lookups. */
    private static final Map<Class<?>, MethodHandle> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

    /** Automatically assigned UUID for {@link EventWithUuid} events. */
    private String eventId = null;

    /** The event instance created by this publisher. */
    private IEvent event = null;

    /** The listener returned by GlobalEventBus subscription. Used for unsubscription. */
    private Object listener;

    /** Flag indicating whether to automatically unsubscribe the listener after success. */
    private boolean unsubscribeAfterSuccess = false;

    /** Holds the results returned from the subscribed event, if any. */
    private Map<String, Object> result = null;

    /** Empty constructor (event must be added via {@link #addPostEvent}). */
    public EventFlow() {}

    /**
     * Instantiate an event of the given class and store it in this publisher.
     */
    public <T extends IEvent> EventFlow addPostEvent(Class<T> eventClass, Object... args) {
        try {
            boolean isUuidEvent = EventWithUuid.class.isAssignableFrom(eventClass);

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

            this.event = (IEvent) ctorHandle.invokeWithArguments(finalArgs);
            return this;

        } catch (Throwable e) {
            throw new RuntimeException("Failed to instantiate event", e);
        }
    }

    /**
     * Start listening for a response event type, chainable with perform().
     */
    public <TT extends IEvent> ResponseBuilder<TT> onResponse(Class<TT> eventClass) {
        return new ResponseBuilder<>(this, eventClass);
    }

    public static class ResponseBuilder<R extends IEvent> {
        private final EventFlow parent;
        private final Class<R> responseClass;

        ResponseBuilder(EventFlow parent, Class<R> responseClass) {
            this.parent = parent;
            this.responseClass = responseClass;
        }

        /** Finalize the subscription */
        public EventFlow perform(Consumer<R> action) {
            parent.listener = GlobalEventBus.subscribe(responseClass, event -> {
                action.accept(responseClass.cast(event));
                if (parent.unsubscribeAfterSuccess && parent.listener != null) {
                    GlobalEventBus.unsubscribe(parent.listener);
                }
            });
            return parent;
        }
    }

    /**
     * Subscribe by ID: only fires if UUID matches this publisher's eventId.
     */
    public <TT extends EventWithUuid> EventFlow onResponse(Class<TT> eventClass, Consumer<TT> action) {
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
     * Subscribe by ID without explicit class.
     */
    @SuppressWarnings("unchecked")
    public <TT extends EventWithUuid> EventFlow onResponse(Consumer<TT> action) {
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

    // choose event type
    public <TT extends IEvent> EventSubscriberBuilder<TT> onEvent(Class<TT> eventClass) {
        return new EventSubscriberBuilder<>(this, eventClass);
    }

    // One-liner shorthand
    public <TT extends IEvent> EventFlow listen(Class<TT> eventClass, Consumer<TT> action) {
        return this.onEvent(eventClass).perform(action);
    }

    // Builder for chaining .onEvent(...).perform(...)
    public static class EventSubscriberBuilder<TT extends IEvent> {
        private final EventFlow publisher;
        private final Class<TT> eventClass;

        EventSubscriberBuilder(EventFlow publisher, Class<TT> eventClass) {
            this.publisher = publisher;
            this.eventClass = eventClass;
        }

        public EventFlow perform(Consumer<TT> action) {
            publisher.listener = GlobalEventBus.subscribe(eventClass, event -> {
                action.accept(eventClass.cast(event));
                if (publisher.unsubscribeAfterSuccess && publisher.listener != null) {
                    GlobalEventBus.unsubscribe(publisher.listener);
                }
            });
            return publisher;
        }
    }

    /** Post synchronously */
    public EventFlow postEvent() {
        GlobalEventBus.post(event);
        return this;
    }

    /** Post asynchronously */
    public EventFlow asyncPostEvent() {
        GlobalEventBus.postAsync(event);
        return this;
    }

    public EventFlow unsubscribeAfterSuccess() {
        this.unsubscribeAfterSuccess = true;
        return this;
    }

    public EventFlow unsubscribeNow() {
        if (unsubscribeAfterSuccess && listener != null) {
            GlobalEventBus.unsubscribe(listener);
        }
        return this;
    }

    public Map<String, Object> getResult() {
        return this.result;
    }

    public IEvent getEvent() {
        return event;
    }

    public String getEventId() {
        return eventId;
    }
}
