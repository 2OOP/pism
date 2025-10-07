package org.toop.framework.resource;

import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.resource.resources.BaseResource;

public class ResourceMeta<T extends BaseResource> {
    private final Long id;
    private final String name;
    private final T resource;

    public ResourceMeta(String name, T resource) {
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
