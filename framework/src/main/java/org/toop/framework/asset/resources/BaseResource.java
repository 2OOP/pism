package org.toop.framework.asset.resources;

import java.io.*;
import java.nio.file.Files;

public abstract class BaseResource {
    private byte[] rawData;
    final File file;
    private boolean isLoaded = false;

    BaseResource(final File file) {
        this.file = file;
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void load() throws FileNotFoundException{
        try {
            this.rawData = Files.readAllBytes(file.toPath());
            isLoaded = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unload() {
        this.rawData = null;
        this.isLoaded = false;
    }

    public File getFile() {
        return this.file;
    }

    public InputStream getInputStream() {
        if (!isLoaded){
            try {
                this.load();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new BufferedInputStream(new ByteArrayInputStream(this.rawData));

    }
}
