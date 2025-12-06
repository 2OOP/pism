package org.toop.framework.eventbus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DisruptorEventsHolder implements EventsHolder {
    private final Map<Class<?>, CopyOnWriteArrayList<ListenerHandler<?>>> LISTENERS = new ConcurrentHashMap<>();

    @Override
    public void add(ListenerHandler<?> listener) {
        LISTENERS.computeIfAbsent(listener.getListenerClass(), _ -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    public void remove(ListenerHandler<?> listener) {
        LISTENERS.getOrDefault(listener.getListenerClass(), new CopyOnWriteArrayList<>()).remove(listener);
        LISTENERS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public List<ListenerHandler<?>> get(Class<?> listenerClass) {
        return LISTENERS.get(listenerClass);
    }

    @Override
    public void reset() {
        LISTENERS.clear();
    }
}
