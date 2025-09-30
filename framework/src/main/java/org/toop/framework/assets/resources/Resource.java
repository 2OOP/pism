package org.toop.framework.assets.resources;

import java.io.*;

public abstract class Resource {
    final InputStream stream;
    final File file;

    Resource(final File file) {
        this.file = file;
        try {
            this.stream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource load() {
        return this;
    }

    public InputStream getInputStream() {
        return this.stream;
    }

    public File getFile() {
        return this.file;
    }
}
