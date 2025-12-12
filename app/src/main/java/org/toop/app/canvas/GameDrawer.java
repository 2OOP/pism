package org.toop.app.canvas;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface GameDrawer {
    void redraw(TurnBasedGame gameCopy);
}
