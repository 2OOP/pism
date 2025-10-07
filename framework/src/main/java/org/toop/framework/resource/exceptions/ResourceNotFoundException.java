package org.toop.framework.resource.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String name) {
        super("Could not find resource: " + name);
    }
}
