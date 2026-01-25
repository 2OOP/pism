package org.toop.app.gameControllers;

import org.toop.app.canvas.ReversiBitCanvas;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.game.gameThreads.LocalThreadBehaviour;
import org.toop.framework.game.gameThreads.OnlineThreadBehaviour;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.game.players.OnlinePlayer;

import java.util.Arrays;

public class ReversiBitController extends GenericGameController {

    private BitboardReversi game;

    public ReversiBitController(Player[] players) {
        BitboardReversi game = new BitboardReversi();
        game.init(players);
        ThreadBehaviour thread = Arrays.stream(players).anyMatch(e -> e instanceof OnlinePlayer) ?
                new OnlineThreadBehaviour(game) : new LocalThreadBehaviour(game);

        super(new ReversiBitCanvas(), game, thread, "Reversi");
    }

    public BitboardReversi.Score getScore() {
        return game.getScore();
    }
}
