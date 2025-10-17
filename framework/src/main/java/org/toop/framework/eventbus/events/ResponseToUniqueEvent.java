package org.toop.framework.eventbus.events;

import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;

/**
 * MUST HAVE long identifier at the end.
 * e.g.
 *
 * <pre>{@code
 * public record uniqueEventResponse(String content, long identifier) implements ResponseToUniqueEvent {};
 * public record uniqueEventResponse(long identifier) implements ResponseToUniqueEvent {};
 * public record uniqueEventResponse(String content, int number, long identifier) implements ResponseToUniqueEvent {};
 * }</pre>
 *
 */
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
