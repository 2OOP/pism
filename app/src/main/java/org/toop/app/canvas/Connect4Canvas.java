package org.toop.app.canvas;

import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class Connect4Canvas extends GameCanvas {
    public Connect4Canvas(Color color, int width, int height, Consumer<Integer> onCellClicked) {
        super(color, width, height, 6, 7, 10, true, onCellClicked);
    }


}
