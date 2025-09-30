package org.toop.framework.assets.resources;

import javafx.scene.image.Image;
import java.io.File;

public class ImageResource extends Resource implements ResourceType<ImageResource> {

    private Image image = null;

    public ImageResource(File imageFile) {
        super(imageFile);
    }

    public Image getImage() {
        return this.image;
    }

    public ImageResource load() {
        this.image = new Image(this.stream);
        return this;
    }
}
