package org.toop.app.gameControllers;

import org.toop.app.canvas.TicTacToeBitCanvas;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.game.gameThreads.LocalThreadBehaviour;
import org.toop.framework.game.gameThreads.OnlineThreadBehaviour;
import org.toop.framework.game.games.tictactoe.BitboardTicTacToe;
import org.toop.framework.game.players.OnlinePlayer;

public class TicTacToeBitController extends GenericGameController {
    public TicTacToeBitController(Player[] players) {
        BitboardTicTacToe game = new BitboardTicTacToe(players);
        ThreadBehaviour thread = new LocalThreadBehaviour(game);
        for (Player player : players) {
            if (player instanceof OnlinePlayer){
                thread = new OnlineThreadBehaviour(game);
            }
        }
        super(new TicTacToeBitCanvas(), game, thread , "TicTacToe");
    }
}
