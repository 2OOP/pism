package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.complex.ViewWidget;
import java.io.File;

public class TicTacToeTutorialWidget extends ViewWidget {

    private TState state;
    private String[] keys = {"tictactoe1", "tictactoe2"};
    private File[] images = {
            new File("app/src/main/resources/assets/images/tictactoe1.png"),
            new File("app/src/main/resources/assets/images/tictactoe2.png")
    };
    private BaseTutorialWidget tutorialWidget;

    public TicTacToeTutorialWidget() {
        System.out.println("Hi, I got here!");
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
        System.out.println("Hi, I got to the end!");
        add(Pos.CENTER, tutorialWidget);
    }

    private void update() {
        tutorialWidget.update(keys[state.getCurrent()], images[state.getCurrent()]);
    }
}
