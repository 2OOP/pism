package org.toop.framework.eventbus.subscriber;

import java.util.function.Consumer;

public class DefaultSubscriber<T, K> implements Subscriber<K, T> {
    private final K id;
    private final Class<T> event;
    private final Consumer<T> action;

    public DefaultSubscriber(K id, Class<T> eventClass, Consumer<T> action) {
        this.id = id;
        this.event = eventClass;
        this.action = action;
    }

    @Override
    public K getId() {
        return id;
    }

    @Override
    public Class<T> getEvent() {
        return event;
    }

    @Override
    public Consumer<T> getAction() {
        return action;
    }
}
