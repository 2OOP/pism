package org.toop.framework.eventbus.subscriber;

import java.util.function.Consumer;

public interface Subscriber<ID, K> {
    ID id();
    Class<K> event();
    Consumer<K> handler();
}
