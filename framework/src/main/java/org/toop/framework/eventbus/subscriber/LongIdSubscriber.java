package org.toop.framework.eventbus.subscriber;

import java.util.function.Consumer;

public record LongIdSubscriber<K>(Long id, Class<K> event, Consumer<K> handler) implements IdSubscriber<K> {}
