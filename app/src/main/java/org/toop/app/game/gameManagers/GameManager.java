package org.toop.app.game.gameManagers;

import org.toop.framework.gui.GUIEvents;
import org.toop.app.canvas.GameCanvas;
import org.toop.game.TurnBasedGameController;
import org.toop.app.game.UpdatesGameUI;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;

public abstract class GameManager implements UpdatesGameUI {
    // Reference to primary view
    protected final GameView primary;

    // Reference to game canvas
    protected final GameCanvas canvas;

    // Reference to gameThread
    protected final TurnBasedGameController gameThread;

    // TODO: Change gameType to automatically happen with either dependency injection or something else.
    protected GameManager(GameCanvas canvas, TurnBasedGameController gameThread, String gameType) {
        this.canvas = canvas;
        this.gameThread = gameThread;
        primary = new GameView(null, null, null, gameType);

        addListeners();
    }

    private void addListeners(){
        // Listen to requests to update game UI
        new EventFlow().listen(this::onUpdateGameUI);
    }

    private void onUpdateGameUI(GUIEvents.UpdateGameCanvas event){
        this.updateUI();
    }
}
