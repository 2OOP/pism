package org.toop.app.widget.tutorial;

import javafx.application.Platform;
import javafx.geometry.Pos;
import org.apache.maven.surefire.shared.lang3.tuple.ImmutablePair;
import org.toop.app.GameInformation;
import org.toop.app.game.ReversiGame;
import org.toop.app.game.TicTacToeGameThread;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.ImageAsset;

import java.io.File;
import java.util.List;

public class TicTacToeTutorialWidget extends BaseTutorialWidget {

    public TicTacToeTutorialWidget(GameInformation gameInformation) {
        super(List.of(
                new ImmutablePair<>("tictactoe1", ResourceManager.get("tictactoe1.png")),
                new ImmutablePair<>("tictactoe2", ResourceManager.get("tictactoe2.png"))
        ), () -> Platform.runLater(() -> new TicTacToeGameThread(gameInformation)));
    }

    public TicTacToeTutorialWidget() {
        super(List.of(
                new ImmutablePair<>("tictactoe1", ResourceManager.get("tictactoe1.png")),
                new ImmutablePair<>("tictactoe2", ResourceManager.get("tictactoe2.png"))
        ), () -> {});
    }

}
