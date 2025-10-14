package org.toop.framework.resource.resources;

import java.io.*;

public abstract class BaseResource {

    final File file;
    boolean isLoaded = false;

    protected BaseResource(final File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }
}
