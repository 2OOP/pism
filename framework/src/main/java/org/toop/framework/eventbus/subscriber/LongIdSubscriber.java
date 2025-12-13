package org.toop.framework.eventbus.subscriber;

import org.toop.framework.eventbus.events.EventType;

import java.util.function.Consumer;

public record LongIdSubscriber<K extends EventType>(Long id, Class<K> event, Consumer<K> handler)
        implements IdSubscriber<K> {}
