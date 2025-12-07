package org.toop.framework.eventbus.bus;

import org.toop.framework.eventbus.subscriber.Subscriber;

public interface EventBus {
    void subscribe(Subscriber<?, ?> subscriber);
    void unsubscribe(Subscriber<?, ?> subscriber);
    <T> void post(T event);
    void shutdown();
    void reset();
}
