package org.toop.framework.eventbus.bus;

import org.toop.framework.eventbus.events.EventType;

public interface AsyncEventBus extends EventBus {
    <T extends EventType> void asyncPost(T event);
}
