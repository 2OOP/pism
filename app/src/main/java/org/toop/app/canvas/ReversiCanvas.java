package org.toop.app.canvas;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import org.toop.game.Game;


import java.util.function.Consumer;

public class ReversiCanvas extends GameCanvas{
    private Game.Move[] mostRecentLegalMoves;
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
        mostRecentLegalMoves = moves;
        for(Game.Move move : moves){
            IO.println("Legal Moves:" + move.position());
            drawDot(new Color(1f,0,0,0.25f),move.position());
        }
    }
    public void removeLegalMoves(){
        if (mostRecentLegalMoves != null){
            for(Game.Move move : mostRecentLegalMoves){
                drawDot(Color.GRAY,move.position()); //todo get current background color or make this redraw the entire board
            }
        }
        mostRecentLegalMoves = null;
    }
}
