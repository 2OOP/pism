package org.toop.framework.eventbus.subscriber;

import java.util.function.Consumer;

public record DefaultSubscriber<K>(String id, Class<K> event, Consumer<K> handler) implements NamedSubscriber<K> {}
