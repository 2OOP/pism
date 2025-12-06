package org.toop.framework.eventbus.holder;

import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AsyncEventsHolder implements EventsHolder {
    private final Map<Class<?>, CopyOnWriteArrayList<Subscriber<?, ?>>> LISTENERS = new ConcurrentHashMap<>();

    @Override
    public void add(Subscriber<?, ?> sub) {
        LISTENERS.computeIfAbsent(sub.getEvent(), _ -> new CopyOnWriteArrayList<>()).add(sub);
    }

    @Override
    public void remove(Subscriber<?, ?> sub) {
        LISTENERS.getOrDefault(sub.getEvent(), new CopyOnWriteArrayList<>()).remove(sub);
        LISTENERS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public List<Subscriber<?, ?>> get(Class<?> event) {
        return LISTENERS.get(event);
    }

    @Override
    public void reset() {
        LISTENERS.clear();
    }
}
