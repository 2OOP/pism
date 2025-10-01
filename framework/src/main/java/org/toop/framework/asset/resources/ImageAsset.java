package org.toop.framework.asset.resources;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;

@FileExtension({"png", "jpg", "jpeg"})
public class ImageAsset extends BaseResource implements LoadableResource {
    private Image image;

    public ImageAsset(final File file) {
        super(file);
    }

    @Override
    public void load() {
        if (!this.isLoaded) {
            try (FileInputStream fis = new FileInputStream(this.file)) {
                this.image = new Image(fis);
                this.isLoaded = true;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load image: " + this.file, e);
            }
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
        if (!this.isLoaded) {
            this.load();
            return image;
        }
        return null;
    }
}