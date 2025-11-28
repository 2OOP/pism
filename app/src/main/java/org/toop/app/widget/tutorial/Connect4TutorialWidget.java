package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.complex.ViewWidget;

import java.io.File;

public class Connect4TutorialWidget extends ViewWidget {
    private TState state;
    private String[] keys = {"connect4.1", "connect4.2"};
    private File[] images = {new File("app/src/main/resources/assets/images/connect41.png"), new File("app/src/main/resources/assets/images/connect42.png")};
    private BaseTutorialWidget tutorialWidget;

    public Connect4TutorialWidget() {
        this.state = new TState(keys.length);
        tutorialWidget = new BaseTutorialWidget(
                state,
                keys[state.getCurrent()],
                images[state.getCurrent()],
                () -> {
                    if (state.hasPrevious()) {
                        state.previous();
                        update();
                    }
                },
                () -> {
                    if (state.hasNext()) {
                        state.next();
                        update();
                    }
                }
        );
        add(Pos.CENTER, tutorialWidget);
    }

    private void update() {
        tutorialWidget.update(keys[state.getCurrent()], images[state.getCurrent()]);
    }
}
