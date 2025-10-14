package org.toop.framework.eventbus.events;

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
