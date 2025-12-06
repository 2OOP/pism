package org.toop.framework.eventbus.holder;

import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.List;

public interface EventsHolder {
    void add(Subscriber<?, ?> listener);
    void remove(Subscriber<?, ?> listener);
    List<Subscriber<?, ?>> get(Class<?> listenerClass);
    void reset();
}
