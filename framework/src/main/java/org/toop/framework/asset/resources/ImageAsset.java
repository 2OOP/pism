package org.toop.framework.asset.resources;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FileNotFoundException;

@FileExtension({"png"})
public class ImageAsset extends BaseResource implements LoadableResource {
    private Image image;
    private boolean isLoaded = false;

    public ImageAsset(final File file) {
        super(file);
    }

    @Override
    public void load() {
        if (!this.isLoaded) {
            this.image = new Image(this.stream);
            this.isLoaded = true;
        }
    }

    @Override
    public void unload() {
        this.image = null;
        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    public Image getImage() {
        if (!this.isLoaded) load();
        return image;
    }
}
