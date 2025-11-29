package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.complex.ViewWidget;

import java.io.File;

public class ReversiTutorialWidget extends ViewWidget {
    private TState state;
    private String[] keys = {"reversi1", "reversi2", "reversi3", "reversi4"};
    private File[] images = {new File("app/src/main/resources/assets/images/reversi1.png"), new File("app/src/main/resources/assets/images/reversi2.png"),  new File("app/src/main/resources/assets/images/cat.jpg"), new File("app/src/main/resources/assets/images/cat.jpg")};
    private BaseTutorialWidget tutorialWidget;

    public ReversiTutorialWidget() {
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
