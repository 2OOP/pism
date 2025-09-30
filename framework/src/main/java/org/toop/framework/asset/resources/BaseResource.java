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

    public void load() throws FileNotFoundException {
        this.loadRawData();
        isLoaded = true;
    }

    private void loadRawData() throws FileNotFoundException{
        try {
            this.rawData = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unload(){
        this.unloadRawData();
        isLoaded = false;
    }

    private void unloadRawData() {
        this.rawData = null;
    }

    public File getFile() {
        return this.file;
    }

    public InputStream getInputStream() throws FileNotFoundException {
        if (!isLoaded){
            // Manually load the data, makes sure it doesn't call subclass load()
            loadRawData();
            isLoaded = true;
        }
        return new BufferedInputStream(new ByteArrayInputStream(this.rawData));
    }
}
