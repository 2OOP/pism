package org.toop.framework.resource.exceptions;

import java.util.Map;

public class CouldNotCreateResourceFactoryException extends RuntimeException {
    public CouldNotCreateResourceFactoryException(Map<?, ?> registry, String fileName) {
        super(
                String.format(
                        "Could not create resource factory for: %s, isRegistryEmpty: %b",
                        fileName, registry.isEmpty()));
    }
}
