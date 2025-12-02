package org.toop.framework.resource.resources;

import java.io.File;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import org.toop.framework.resource.types.FileExtension;
import org.toop.framework.resource.types.LoadableResource;

@FileExtension({"png", "jpg", "jpeg"})
public class ImageAsset extends BaseResource implements LoadableResource {
    private Image image = null;

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
        }
        return image;
    }
}
