package org.toop.framework.eventbus;

import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.eventbus.events.EventType;

import java.util.function.Consumer;

public class ListenerHandler<T extends EventType> {
    private final long id;
    private final String name;
    private final Class<T> clazz;
    private final Consumer<T> listener;

    public ListenerHandler(long id, String name, Class<T> clazz, Consumer<T> listener) {
        this.id = id;
        this.name = name;
        this.clazz = clazz;
        this.listener = listener;
    }

    public ListenerHandler(String name, Class<T> clazz, Consumer<T> listener) {
        this(SnowflakeGenerator.nextId(), name, clazz, listener);
    }

    public ListenerHandler(long id, Class<T> clazz, Consumer<T> listener) {
        this(id, String.valueOf(id), clazz, listener);
    }

    public ListenerHandler(Class<T> clazz, Consumer<T> listener) {
        this(SnowflakeGenerator.nextId(), clazz, listener);
    }

    public long getId() {
        return id;
    }

    public Consumer<T> getListener() {
        return listener;
    }

    public Class<? extends EventType> getListenerClass() {
        return clazz;
    }

    public String getName() {
        return name;
    }
}
