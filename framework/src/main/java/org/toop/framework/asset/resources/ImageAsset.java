package org.toop.framework.asset.resources;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FileNotFoundException;

public class ImageAsset extends BaseResource implements LoadableResource {
    private Image image;
    private boolean isLoaded = false;

    public ImageAsset(final File file) throws FileNotFoundException {
        super(file);
    }

    @Override
    public void load() throws FileNotFoundException {
        if (!this.isLoaded) {
            this.image = new Image(this.getInputStream());
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
        if (!this.isLoaded) try {
            load();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
}
