package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.ImageAsset;

import java.io.File;

public class ReversiTutorialWidget extends BaseTutorialWidget {
    private final String[] keys;
    private final ImageAsset[] images;

    public ReversiTutorialWidget() {
        String[] newKeys = {"reversi1", "reversi2", "reversi3", "reversi4"};
        ImageAsset[] newImages = {
                ResourceManager.get("reversi1.png"),
                ResourceManager.get("reversi2.png"),
                ResourceManager.get("cat.jpg"),
                ResourceManager.get("cat.jpg")
        };

        super(newKeys[0]);

        keys = newKeys;
        images = newImages;

        setTutorial(
            images[0],
            () -> update(false, keys, images),
            () -> update(true, keys, images)
        );
    }

    public String[] getKeys() {
        return keys;
    }

    public ImageAsset[] getImages() {
        return images;
    }
}
