package org.toop.eventbus.events;

import java.util.Map;

public interface EventWithUuid {
    Map<String, Object> result();
    String eventId();
}
