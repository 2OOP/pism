package org.toop.framework.eventbus.subscriber;

import org.toop.framework.eventbus.events.EventType;

import java.util.function.Consumer;

public interface Subscriber<K extends EventType> {
    Class<K> event();
    Consumer<K> handler();
}
