package org.toop.framework.assets.resources;

import java.io.FileNotFoundException;

public interface LoadableResource {
    void load() throws FileNotFoundException;
    void unload();
    boolean isLoaded();
}
