package org.toop.framework.resource.exceptions;

public class IsNotAResourceException extends RuntimeException {
    public <T> IsNotAResourceException(Class<T> clazz, String message) {
        super(clazz.getName() + " does not implement BaseResource");
    }
}
