package org.toop.app.gameControllers;

import org.toop.app.canvas.TicTacToeBitCanvas;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.game.gameThreads.LocalThreadBehaviour;
import org.toop.framework.game.gameThreads.OnlineThreadBehaviour;
import org.toop.framework.game.games.tictactoe.BitboardTicTacToe;
import org.toop.framework.game.players.OnlinePlayer;

import java.util.Arrays;

public class TicTacToeBitController extends GenericGameController {
    public TicTacToeBitController(Player[] players) {
        BitboardTicTacToe game = new BitboardTicTacToe();
        game.init(players);

        ThreadBehaviour thread = Arrays.stream(players).anyMatch(e -> e instanceof OnlinePlayer) ?
                new OnlineThreadBehaviour(game) : new LocalThreadBehaviour(game);

        super(new TicTacToeBitCanvas(), game, thread, "TicTacToe");
    }
}
