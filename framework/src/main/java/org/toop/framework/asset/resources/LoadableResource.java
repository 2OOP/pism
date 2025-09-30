package org.toop.framework.asset.resources;

import java.io.FileNotFoundException;

public interface LoadableResource {
    void load() throws FileNotFoundException;
    void unload();
    boolean isLoaded();
}
