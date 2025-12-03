package org.toop.app.canvas;

import javafx.scene.paint.Color;
import org.toop.framework.gameFramework.model.game.AbstractGame;

import java.util.function.Consumer;

public class Connect4Canvas extends GameCanvas {
    public Connect4Canvas(Color color, int width, int height, Consumer<Integer> onCellClicked) {
        super(color, Color.TRANSPARENT, width, height, 7, 6, 10, true, onCellClicked,null);
    }

    @Override
    public void drawPlayerHover(int player, int move, AbstractGame game) {

    }
}