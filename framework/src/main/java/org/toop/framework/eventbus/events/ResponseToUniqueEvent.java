package org.toop.framework.eventbus.events;

import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;

public interface ResponseToUniqueEvent extends UniqueEvent {
    default Map<String, Object> result() {
        Map<String, Object> map = new HashMap<>();
        try {
            for (RecordComponent component : this.getClass().getRecordComponents()) {
                Object value = component.getAccessor().invoke(this);
                map.put(component.getName(), value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to build result map via reflection", e);
        }
        return Map.copyOf(map);
    }
}
