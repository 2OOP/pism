package org.toop.app.layer.layers.game;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.ReversiCanvas;
import org.toop.app.layer.*;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.app.layer.layers.MainLayer;
import org.toop.game.Game;
import org.toop.game.reversi.Reversi;
import org.toop.game.reversi.ReversiAI;
import org.toop.local.AppContext;

public class ReversiLayer extends Layer{
    private ReversiCanvas canvas;
    private Reversi reversi;
    private ReversiAI reversiAI;
    public ReversiLayer(){
        super("bg-secondary"); //make reversiboard background dark green

        canvas = new ReversiCanvas(Color.GREEN,(App.getHeight() / 100) * 75, (App.getHeight() / 100) * 75, (cell) -> {
            reversi.play(new Game.Move(cell,reversi.getCurrentPlayer()));
            reload();
            canvas.drawLegalMoves(reversi.getLegalMoves());
        });
        reversi = new Reversi() ;
        reversiAI = new ReversiAI();


        reload();
        canvas.drawLegalMoves(reversi.getLegalMoves());
    }

    @Override
    public void reload() {
        popAll();
        canvas.resize((App.getHeight() / 100) * 75, (App.getHeight() / 100) * 75);

        for (int i = 0; i < reversi.board.length; i++) {
            final char value = reversi.board[i];

            if (value == 'B') {
                canvas.drawDot(Color.BLACK, i);
            } else if (value == 'W') {
                canvas.drawDot(Color.WHITE, i);
            }
        }

        final var backButton = NodeBuilder.button(AppContext.getString("back"), () -> {
            App.activate(new MainLayer());
        });

        final Container controlContainer = new VerticalContainer(5);
        controlContainer.addNodes(backButton);

        final Container informationContainer = new HorizontalContainer(15);

        addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
        addContainer(informationContainer, Pos.TOP_LEFT, 2, 2, 0, 0);
        addGameCanvas(canvas, Pos.CENTER, 0, 0);
    }
}
