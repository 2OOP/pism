package org.toop.app.game;

import org.toop.app.canvas.GameCanvas;
import org.toop.app.game.TurnBasedGameThread;
import org.toop.app.widget.view.GameView;

public abstract class GameController implements UpdatesGameUI {
    // Reference to primary view
    protected final GameView primary;

    // Reference to game canvas
    protected final GameCanvas canvas;

    // Reference to gameThread
    protected TurnBasedGameThread gameThread;

    // TODO: Change gameType to automatically happen with either dependency injection or something else.
    protected GameController(GameCanvas canvas, String gameType) {
        this.canvas = canvas;
        primary = new GameView(null, null, null, gameType);
    }

    protected void setThread(TurnBasedGameThread gameThread){
        this.gameThread = gameThread;
    }
}
