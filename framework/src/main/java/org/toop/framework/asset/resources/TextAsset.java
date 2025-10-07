package org.toop.framework.asset.resources;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.toop.framework.asset.types.FileExtension;
import org.toop.framework.asset.types.LoadableResource;

@FileExtension({"txt", "json", "xml"})
public class TextAsset extends BaseResource implements LoadableResource {
    private String content;

    public TextAsset(File file) {
        super(file);
    }

    @Override
    public void load() {
        try {
            byte[] bytes = Files.readAllBytes(getFile().toPath());
            this.content = new String(bytes, StandardCharsets.UTF_8);
            this.isLoaded = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load text asset: " + getFile(), e);
        }
    }

    @Override
    public void unload() {
        this.content = null;
        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    public String getContent() {
        return this.content;
    }
}
