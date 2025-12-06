package org.toop.framework.eventbus;

import org.toop.framework.eventbus.events.EventType;

public interface EventBus<E> {
    void subscribe(ListenerHandler<? extends EventType> listener);
    void unsubscribe(ListenerHandler<? extends EventType> listener);
    void post(EventType event);
    void shutdown();
    void reset();
}
