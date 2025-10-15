package org.toop.framework.eventbus.events;

/**
 * MUST HAVE long identifier at the end.
 * e.g.
 *
 * <pre>{@code
 * public record uniqueEvent(String content, long identifier) implements UniqueEvent {};
 * public record uniqueEvent(long identifier) implements UniqueEvent {};
 * public record uniqueEvent(String content, int number, long identifier) implements UniqueEvent {};
 * }</pre>
 *
 */
public interface UniqueEvent extends EventType {
    default long getIdentifier() {
        try {
            var method = this.getClass().getMethod("identifier");
            return (long) method.invoke(this);
        } catch (Exception e) {
            throw new RuntimeException("No identifier accessor found", e);
        }
    }
}
