package org.toop.app.canvas;

import javafx.scene.canvas.Canvas;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface GameCanvas<T extends TurnBasedGame<T>> extends GameDrawer<T>{
    Canvas getCanvas();
}
