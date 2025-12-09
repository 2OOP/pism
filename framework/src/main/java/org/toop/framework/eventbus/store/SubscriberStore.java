package org.toop.framework.eventbus.store;

import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.subscriber.Subscriber;

public interface SubscriberStore {
    void add(Subscriber<? extends EventType> subscriber);
    void remove(Subscriber<? extends EventType> subscriber);
    Subscriber<? extends EventType>[] get(Class<? extends EventType> event);
    void reset();
}
