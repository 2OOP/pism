package org.toop.app.canvas;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface GameDrawer<T extends TurnBasedGame<T>> {
    void redraw(T gameCopy);
}
