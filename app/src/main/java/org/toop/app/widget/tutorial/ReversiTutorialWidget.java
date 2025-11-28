package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.complex.ViewWidget;

import java.io.File;

public class ReversiTutorialWidget extends BaseTutorialWidget {
    private final String[] keys;
    private final File[] images = {new File("app/src/main/resources/assets/images/reversi1.png"), new File("app/src/main/resources/assets/images/reversi2.png"),  new File("app/src/main/resources/assets/images/cat.jpg"), new File("app/src/main/resources/assets/images/cat.jpg")};

    public ReversiTutorialWidget() {
        String[] newKeys = {"reversi1", "reversi2", "reversi3", "reversi4"};

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
