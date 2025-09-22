package org.toop.eventbus.events;

import java.util.Map;

public interface EventWithUuid extends IEvent {
    Map<String, Object> result();
    String eventId();
}
