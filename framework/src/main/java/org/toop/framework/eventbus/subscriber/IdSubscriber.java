package org.toop.framework.eventbus.subscriber;

import org.toop.framework.eventbus.events.EventType;

public interface IdSubscriber<K extends EventType> extends Subscriber<K>, HasId<Long> {}
