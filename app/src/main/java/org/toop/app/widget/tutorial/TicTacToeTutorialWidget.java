package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.ImageAsset;

import java.io.File;

public class TicTacToeTutorialWidget extends BaseTutorialWidget {

    private final String[] keys;
    private final ImageAsset[] images;

    public TicTacToeTutorialWidget() {
        String[] newKeys = {"tictactoe1", "tictactoe2"};
        ImageAsset[] newImages = {
                ResourceManager.get("tictactoe1.png"),
                ResourceManager.get("tictactoe2.png")
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
