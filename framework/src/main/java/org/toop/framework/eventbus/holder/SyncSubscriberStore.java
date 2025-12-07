package org.toop.framework.eventbus.holder;

import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncSubscriberStore implements SubscriberStore {
    private final Map<Class<?>, List<Subscriber<?, ?>>> LISTENERS = new ConcurrentHashMap<>();
    private static final Subscriber<?, ?>[] EMPTY = new Subscriber[0];

    @Override
    public void add(Subscriber<?, ?> sub) {
        LISTENERS.computeIfAbsent(sub.event(), _ -> new ArrayList<>()).add(sub);
    }

    @Override
    public void remove(Subscriber<?, ?> sub) {
        LISTENERS.getOrDefault(sub.event(), new ArrayList<>()).remove(sub);
        LISTENERS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public Subscriber<?, ?>[] get(Class<?> event) {
        List<Subscriber<?, ?>> list = LISTENERS.get(event);
        if (list == null || list.isEmpty()) return EMPTY;
        return list.toArray(EMPTY);
    }

    @Override
    public void reset() {
        LISTENERS.clear();
    }
}
