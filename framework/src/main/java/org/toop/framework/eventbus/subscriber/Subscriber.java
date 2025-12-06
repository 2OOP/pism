package org.toop.framework.eventbus.subscriber;

import java.util.function.Consumer;

public interface Subscriber<T, K> {
    T getId();
    Class<K> getEvent();
    Consumer<K> getAction();
}
