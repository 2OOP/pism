package org.toop.framework.eventbus.store;

import org.toop.framework.eventbus.subscriber.Subscriber;

public interface SubscriberStore {
    void add(Subscriber<?, ?> subscriber);
    void remove(Subscriber<?, ?> subscriber);
    Subscriber<?, ?>[] get(Class<?> event);
    void reset();
}
