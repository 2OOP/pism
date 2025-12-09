package org.toop.framework.eventbus.subscriber;

import org.toop.framework.eventbus.events.EventType;

public interface NamedSubscriber<K extends EventType> extends Subscriber<K>, HasId<String> {}
