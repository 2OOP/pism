package org.toop.app.game;

import org.toop.app.canvas.GameCanvas;
import org.toop.app.game.TurnBasedGameThread;
import org.toop.app.widget.view.GameView;

public abstract class GameController implements UpdatesGameUI {
    // Reference to primary view
    protected final GameView primary = new GameView(null, null, null);

    // Reference to game canvas
    protected final GameCanvas canvas;

    // Reference to gameThread
    protected TurnBasedGameThread gameThread;

    protected GameController(GameCanvas canvas) {
        this.canvas = canvas;
    }

    protected void setThread(TurnBasedGameThread gameThread){
        this.gameThread = gameThread;
    }
}
