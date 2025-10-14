package org.toop.app.canvas;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import org.toop.game.Game;


import java.util.function.Consumer;

public class ReversiCanvas extends GameCanvas{
    public ReversiCanvas(Color color, int width, int height, Consumer<Integer> onCellClicked) {
        super(color, width, height, 8, 8, 10, true, onCellClicked);
        drawStartingDots();
    }
    public void drawStartingDots(){
        drawDot(Color.BLACK,28);
        drawDot(Color.WHITE,36);
        drawDot(Color.BLACK,35);
        drawDot(Color.WHITE,27);
    }
    public void drawLegalMoves(Game.Move[] moves){
        for(Game.Move move : moves){
            drawDot(new Color(1f,0,0,0.25f),move.position());
        }
    }
}
