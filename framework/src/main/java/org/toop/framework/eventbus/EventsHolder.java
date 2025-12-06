package org.toop.framework.eventbus;

import java.util.List;

public interface EventsHolder {
    void add(ListenerHandler<?> listener);
    void remove(ListenerHandler<?> listener);
    List<ListenerHandler<?>> get(Class<?> listenerClass);
    void reset();
}
