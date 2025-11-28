package org.toop.app.widget.tutorial;

import javafx.application.Platform;
import org.apache.maven.surefire.shared.lang3.tuple.ImmutablePair;
import org.toop.app.GameInformation;
import org.toop.app.game.Connect4Game;
import org.toop.framework.resource.ResourceManager;

import java.util.List;

public class Connect4TutorialWidget extends BaseTutorialWidget {
    public Connect4TutorialWidget(Runnable nextScreen) {
        super(List.of(
                new ImmutablePair<>("connect4.1", ResourceManager.get("connect41.png")),
                new ImmutablePair<>("connect4.2", ResourceManager.get("connect42.png"))
        ), nextScreen);
    }
}
