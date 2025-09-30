package org.toop.framework.assets;

import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.assets.resources.Resource;

public class Asset <T extends Resource> {
    private final Long id; // IS this needed?
    private final String name;
    private final T resource;

    public Asset(String name, T resource) {
        this.id = new SnowflakeGenerator().nextId();
        this.name = name;
        this.resource = resource;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public T getResource() {
        return this.resource;
    }

}
