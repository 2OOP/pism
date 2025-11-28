/*package org.toop.game;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.game.Players.LocalPlayer;
import org.toop.game.TurnBasedGameThread;
import org.toop.app.widget.WidgetContainer;

public class TicTacToeController extends GameController {

    public TicTacToeController() {
        super(new TicTacToeCanvas(Color.GRAY,
                (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,(c) -> {if (players[game.getCurrentTurn()] instanceof LocalPlayer lp) {lp.enqueueMove(c);}}), new TurnBasedGameThread());
        primary.add(Pos.CENTER, canvas.getCanvas());
        WidgetContainer.getCurrentView().transitionNext(primary));
    }

    @Override
    public void updateUI() {

    }
}*/
