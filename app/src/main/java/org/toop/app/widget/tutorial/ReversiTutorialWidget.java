package org.toop.app.widget.tutorial;

import javafx.application.Platform;
import org.apache.maven.surefire.shared.lang3.tuple.ImmutablePair;
import org.toop.app.GameInformation;
import org.toop.app.game.ReversiGame;
import org.toop.framework.resource.ResourceManager;

import java.util.List;

public class ReversiTutorialWidget extends BaseTutorialWidget {
    public ReversiTutorialWidget(GameInformation gameInformation) {
        super(List.of(
            new ImmutablePair<>("reversi1", ResourceManager.get("reversi1.png")),
            new ImmutablePair<>("reversi2", ResourceManager.get("reversi2.png")),
            new ImmutablePair<>("reversi3", ResourceManager.get("cat.jpg")),
            new ImmutablePair<>("reversi4", ResourceManager.get("cat.jpg"))
        ), () -> Platform.runLater(() -> new ReversiGame(gameInformation)));
    }

    public ReversiTutorialWidget() {
        super(List.of(
                new ImmutablePair<>("reversi1", ResourceManager.get("reversi1.png")),
                new ImmutablePair<>("reversi2", ResourceManager.get("reversi2.png")),
                new ImmutablePair<>("reversi3", ResourceManager.get("cat.jpg")),
                new ImmutablePair<>("reversi4", ResourceManager.get("cat.jpg"))
        ), () -> {});
    }
}
