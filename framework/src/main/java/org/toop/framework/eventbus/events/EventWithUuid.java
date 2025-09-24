package org.toop.framework.eventbus.events;

import java.util.Map;

public interface EventWithUuid extends EventType {
    Map<String, Object> result();
    String eventId();
}
