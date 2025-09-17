package org.toop.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.function.Consumer;

/**
 * A singleton Event Bus to be used for creating, triggering and activating events.
 */
public class GlobalEventBus {

    /**
     * Singleton event bus.
     */
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

    /**
     * Reset back to the default global EventBus.
     */
    public static void reset() {
        eventBus = new EventBus("global-bus");
    }

    /**
     * Wraps a Consumer into a Guava @Subscribe-compatible listener.
     * TODO
     *
     * @param type The event to be used. (e.g. Events.ServerCommand.class)
     * @param action The function, or lambda to run when fired.
     * @return Object to be used for registering an event.
     */
    private static <T> Object subscribe(Class<T> type, Consumer<T> action) {
        return new Object() {
            @Subscribe
            public void handle(Object event) {
            if (type.isInstance(event)) {
                action.accept(type.cast(event));
            }
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
    public static <T> EventMeta subscribeAndRegister(Class<T> type, Consumer<T> action) {
        Object listener = new Object() {
            @Subscribe
            public void handle(Object event) {
            if (type.isInstance(event)) {
                action.accept(type.cast(event));
            }
            }
        };
        var re = new EventMeta<>(type, listener);
        register(re);
        return re;
    }


    /**
     * Wrapper for registering a listener.
     *
     * @param event The ready event to add to register.
     */
    public static <T> void register(EventMeta<T> event) {
        GlobalEventBus.get().register(event.getEvent());
        event.setReady(true);
        EventRegistry.markReady(event.getType());
    }

    /**
     * Wrapper for unregistering a listener.
     *
     * @param event The ready event to unregister.
     */
    public static <T> void unregister(EventMeta<T> event) {
        EventRegistry.markNotReady(event.getType());
        event.setReady(false);
        GlobalEventBus.get().unregister(event.getEvent());
    }

    /**
     * Wrapper for posting events.
     *
     * @param event The event to post.
     */
    public static <T> void post(T event) {
        Class<T> type = (Class<T>) event.getClass();

//        if (!EventRegistry.isReady(type)) {
//            throw new IllegalStateException("Event type not ready: " + type.getSimpleName());
//        } TODO: Handling non ready events.

        // store in registry
        EventMeta<T> eventMeta = new EventMeta<>(type, event);
        EventRegistry.storeEvent(eventMeta);

        // post to Guava EventBus
        GlobalEventBus.get().post(event);
    }

}
