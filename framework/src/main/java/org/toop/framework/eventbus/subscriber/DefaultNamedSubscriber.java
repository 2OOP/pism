package org.toop.framework.eventbus.subscriber;

import org.toop.framework.eventbus.events.EventType;

import java.util.function.Consumer;

public record DefaultNamedSubscriber<K extends EventType>(String id, Class<K> event, Consumer<K> handler)
        implements NamedSubscriber<K> {}
