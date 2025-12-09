package org.toop.framework.eventbus.store;

import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncSubscriberStore implements SubscriberStore {
    private final ConcurrentHashMap<Class<? extends EventType>, ConcurrentLinkedQueue<Subscriber<? extends EventType>>> queues = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends EventType>, Subscriber<? extends EventType>[]> snapshots = new ConcurrentHashMap<>();

    @Override
    public void add(Subscriber<? extends EventType> sub) {
        queues.computeIfAbsent(sub.event(), _ -> new ConcurrentLinkedQueue<>()).add(sub);
        rebuildSnapshot(sub.event());
    }

    @Override
    public void remove(Subscriber<? extends EventType> sub) {
        ConcurrentLinkedQueue<Subscriber<?>> queue = queues.get(sub.event());
        if (queue != null) {
            queue.remove(sub);
            rebuildSnapshot(sub.event());
        }
    }

    @Override
    public Subscriber<? extends EventType>[] get(Class<? extends EventType> event) {
        return snapshots.getOrDefault(event, new Subscriber<?>[0]);
    }

    @Override
    public void reset() {
        queues.clear();
        snapshots.clear();
    }

    private void rebuildSnapshot(Class<? extends EventType> event) {
        ConcurrentLinkedQueue<Subscriber<?>> queue = queues.get(event);
        if (queue != null) {
            snapshots.put(event, queue.toArray(new Subscriber<?>[0]));
        } else {
            snapshots.put(event, new Subscriber<?>[0]);
        }
    }
}