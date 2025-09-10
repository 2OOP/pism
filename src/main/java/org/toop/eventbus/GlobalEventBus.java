package org.toop.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.function.Consumer;

/**
 * A singleton Event Bus to be used for creating, triggering and activating events.
 */
public enum GlobalEventBus {
    /**
     * The instance of the Event Bus.
     */
    INSTANCE;

    /**
     * Singleton event bus.
     */
    private final EventBus eventBus = new EventBus("global-bus");

    /**
     * Wraps a Consumer into a Guava @Subscribe-compatible listener.
     *
     * @return Singleton Event Bus
     */
    public EventBus get() {
        return eventBus;
    }

    /**
     * Wraps a Consumer into a Guava @Subscribe-compatible listener.
     *
     * @param type The event to be used. (e.g. Events.ServerCommand.class)
     * @param action The function, or lambda to run when fired.
     * @return Object to be used for registering an event.
     */
    public static <T> Object subscribe(Class<T> type, Consumer<T> action) {
        return new Object() {
            @Subscribe
            public void handle(T event) {
                action.accept(event);
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
        Object listener = new Object() {
            @Subscribe
            public void handle(Object event) {
                if (type.isInstance(event)) {
                    action.accept(type.cast(event));
                }
            }
        };
        register(listener);
        return listener;
    }


    /**
     * Wrapper for registering a listener.
     *
     * @param listener The event listener to register.
     */
    public static void register(Object listener) {
        GlobalEventBus.INSTANCE.get().register(listener);
    }

    /**
     * Wrapper for unregistering a listener.
     *
     * @param listener The event listener to unregister.
     */
    public static void unregister(Object listener) {
        GlobalEventBus.INSTANCE.get().unregister(listener);
    }

    public static void post(Object event) {
        GlobalEventBus.INSTANCE.get().post(event);
    }

}