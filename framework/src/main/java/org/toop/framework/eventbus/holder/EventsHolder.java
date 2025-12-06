package org.toop.framework.eventbus.holder;

import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.List;

public interface EventsHolder {
    void add(Subscriber<?, ?> subscriber);
    void remove(Subscriber<?, ?> subscriber);
    List<Subscriber<?, ?>> get(Class<?> event);
    void reset();
}
