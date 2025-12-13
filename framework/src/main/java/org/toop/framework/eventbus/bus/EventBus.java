package org.toop.framework.eventbus.bus;

import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.subscriber.Subscriber;

public interface EventBus {
    void subscribe(Subscriber<? extends EventType> subscriber);
    void unsubscribe(Subscriber<? extends EventType> subscriber);
    <T extends EventType> void post(T event);
    void shutdown();
    void reset();
}
