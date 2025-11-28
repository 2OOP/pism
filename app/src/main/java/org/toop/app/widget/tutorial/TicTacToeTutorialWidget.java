package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.complex.ViewWidget;
import java.io.File;

public class TicTacToeTutorialWidget extends BaseTutorialWidget {

    private final String[] keys;
    private final File[] images = {
            new File("app/src/main/resources/assets/images/tictactoe1.png"),
            new File("app/src/main/resources/assets/images/tictactoe2.png")
    };

    public TicTacToeTutorialWidget() {
        String[] newKeys = {"tictactoe1", "tictactoe2"};

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
