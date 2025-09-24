package org.toop.framework.eventbus.events;

import java.util.Map;

public interface EventWithSnowflake extends EventType {
    Map<String, Object> result();
    long eventSnowflake();
}
