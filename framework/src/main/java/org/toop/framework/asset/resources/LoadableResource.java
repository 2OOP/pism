package org.toop.framework.asset.resources;

import java.io.FileNotFoundException;

public interface LoadableResource {
    void load();
    void unload();
    boolean isLoaded();
}
