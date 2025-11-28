package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.complex.ViewWidget;

import java.io.File;

public class Connect4TutorialWidget extends BaseTutorialWidget {
    private final String[] keys;

    private final File[] images = {
            new File("app/src/main/resources/assets/images/connect41.png"),
            new File("app/src/main/resources/assets/images/connect42.png")
    };

    public Connect4TutorialWidget() {
        String[] newKeys = {
                "connect4.1", "connect4.2"
        };

        super(newKeys[0]);

        keys = newKeys;

        setTutorial(
            images[0],
            () -> update(false, keys, images),
            () -> update(true, keys, images)
        );
    }

    public String[] getKeys() {
        return keys;
    }

    public File[] getImages() {
        return images;
    }
}
