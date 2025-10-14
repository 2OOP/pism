package org.toop.framework.resource.resources;

import java.io.*;
import javafx.scene.media.Media;
import org.toop.framework.resource.types.FileExtension;
import org.toop.framework.resource.types.LoadableResource;

@FileExtension({"mp3"})
public class MusicAsset extends BaseResource implements LoadableResource {
    private Media media;

    public MusicAsset(final File audioFile) {
        super(audioFile);
    }

    public Media getMedia() {
        if (media == null) {
            media = new Media(file.toURI().toString());
        }
        return media;
    }

    @Override
    public void load() {
        if (media == null) media = new Media(file.toURI().toString());
        this.isLoaded = true;
    }

    @Override
    public void unload() {
        media = null;
        isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }
}
