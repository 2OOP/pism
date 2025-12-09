package org.toop.framework.eventbus.store;

import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncSubscriberStore implements SubscriberStore {
    private final Map<Class<? extends EventType>, List<Subscriber<? extends EventType>>> LISTENERS = new ConcurrentHashMap<>();
    private static final Subscriber<? extends EventType>[] EMPTY = new Subscriber<?>[0];

    @Override
    public void add(Subscriber<? extends EventType> sub) {
        LISTENERS.computeIfAbsent(sub.event(), _ -> new ArrayList<>()).add(sub);
    }

    @Override
    public void remove(Subscriber<? extends EventType> sub) {
        LISTENERS.getOrDefault(sub.event(), new ArrayList<>()).remove(sub);
        LISTENERS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public Subscriber<? extends EventType>[] get(Class<? extends EventType> event) {
        List<Subscriber<? extends EventType>> list = LISTENERS.get(event);
        if (list == null || list.isEmpty()) return EMPTY;
        return list.toArray(EMPTY);
    }

    @Override
    public void reset() {
        LISTENERS.clear();
    }
}
