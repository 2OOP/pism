package org.toop.framework.assets.resources;

import java.io.*;
import java.nio.file.Files;

public abstract class Resource {
    final private byte[] rawData;
    final private File file;

    Resource(final File file) throws RuntimeException {
        this.file = file;
        try {
            this.rawData = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource load() {
        return this;
    }

    public InputStream getStream() {
        return new BufferedInputStream(new ByteArrayInputStream(this.rawData));
    }

    public File getFile() {
        return this.file;
    }
}
