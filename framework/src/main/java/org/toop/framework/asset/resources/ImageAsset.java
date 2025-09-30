package org.toop.framework.asset.resources;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FileNotFoundException;

@FileExtension({"png"})
public class ImageAsset extends BaseResource implements LoadableResource {
    private Image image;

    public ImageAsset(final File file) {
        super(file);
    }

    @Override
    public void load() throws FileNotFoundException {
        if (!this.isLoaded()) {
            super.load(); // Make sure that base class (byte[]) is loaded
            this.image = new Image(this.getInputStream());
        }
    }

    @Override
    public void unload() {
        this.image = null;
        super.unload();
    }

    public Image getImage() {
        if (!this.isLoaded()) try {
            load();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
}
