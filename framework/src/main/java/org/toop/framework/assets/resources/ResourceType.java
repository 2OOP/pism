package org.toop.framework.assets.resources;

public interface ResourceType<T extends Resource> {
    T load();
}
