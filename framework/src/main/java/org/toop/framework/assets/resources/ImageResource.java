package org.toop.framework.assets.resources;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageResource extends Resource {

    private Image image = null;

    public ImageResource(File imageFile) {
        super(imageFile);
    }

    public Image getImage() {
        return this.image;
    }

    @Override
    public Resource load() {
        this.image = new Image(this.stream);
        return this;
    }
}
