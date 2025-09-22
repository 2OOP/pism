package org.toop.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.function.Consumer;

/** A singleton Event Bus to be used for creating, triggering and activating events. */
public class GlobalEventBus {

    /** Singleton event bus. */
    private static EventBus eventBus = new EventBus("global-bus");

    private GlobalEventBus() {}

    /**
     * Wraps a Consumer into a Guava @Subscribe-compatible listener.
     *
     * @return Singleton Event Bus
     */
    public static EventBus get() {
        return eventBus;
    }

    /**
     * ONLY USE FOR TESTING
     *
     * @param newBus
     */
    public static void set(EventBus newBus) {
        eventBus = newBus;
    }

    /** Reset back to the default global EventBus. */
    public static void reset() {
        eventBus = new EventBus("global-bus");
    }

    /**
     * Wraps a Consumer into a Guava @Subscribe-compatible listener. TODO
     *
     * @param type The event to be used. (e.g. Events.ServerCommand.class)
     * @param action The function, or lambda to run when fired.
     * @return Object to be used for registering an event.
     */
    public static <T> Object subscribe(Class<T> type, Consumer<T> action) {
        return new Object() {
            @Subscribe
            public void handle(Object event) {
                if (type.isInstance(event)) {
                    action.accept(type.cast(event));
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> Object subscribe(Consumer<T> action) {
        return new Object() {
            @Subscribe
            public void handle(Object event) {
                try {
                    action.accept((T) event); // unchecked cast
                } catch (ClassCastException ignored) {}
            }
        };
    }

    /**
     * Wraps a Consumer into a Guava @Subscribe-compatible listener and registers it.
     *
     * @param type The event to be used. (e.g. Events.ServerCommand.class)
     * @param action The function, or lambda to run when fired.
     * @return Object to be used for registering an event.
     */
    public static <T> Object subscribeAndRegister(Class<T> type, Consumer<T> action) {
        var listener = subscribe(type, action);
        register(listener);
        return listener;
    }

    public static <T> Object subscribeAndRegister(Consumer<T> action) {
        var listener = subscribe(action);
        register(listener);
        return listener;
    }

    /**
     * Wrapper for registering a listener.
     *
     * @param listener The listener to register.
     */
    public static void register(Object listener) {
        GlobalEventBus.get().register(listener);
    }

    /**
     * Wrapper for unregistering a listener.
     *
     * @param listener The listener to unregister.
     */
    public static void unregister(Object listener) {
        GlobalEventBus.get().unregister(listener);
    }

    /**
     * Wrapper for posting events.
     *
     * @param event The event to post.
     */
    public static <T> void post(T event) {
        GlobalEventBus.get().post(event);
    }
}
