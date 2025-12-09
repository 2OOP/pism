package org.toop.app.widget.tutorial;

import org.apache.maven.surefire.shared.lang3.tuple.ImmutablePair;
import org.toop.framework.resource.ResourceManager;

import java.util.List;

public class TicTacToeTutorialWidget extends BaseTutorialWidget {
    public TicTacToeTutorialWidget(Runnable nextScreen) {
        super(List.of(
                new ImmutablePair<>("tictactoe1", ResourceManager.get("tictactoe1.png")),
                new ImmutablePair<>("tictactoe2", ResourceManager.get("tictactoe2.png"))
        ), nextScreen);
    }

}
