package org.toop.app.gameControllers;

import org.toop.app.canvas.TicTacToeBitCanvas;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.gameThreads.LocalFixedRateThreadBehaviour;
import org.toop.game.gameThreads.LocalThreadBehaviour;
import org.toop.game.gameThreads.OnlineThreadBehaviour;
import org.toop.game.gameThreads.OnlineWithSleepThreadBehaviour;
import org.toop.game.games.tictactoe.BitboardTicTacToe;
import org.toop.game.players.OnlinePlayer;

public class TicTacToeBitController extends GenericGameController<BitboardTicTacToe> {
    public TicTacToeBitController(Player<BitboardTicTacToe>[] players) {
        BitboardTicTacToe game = new BitboardTicTacToe(players);
        ThreadBehaviour thread = new LocalThreadBehaviour<>(game);
        for (Player<BitboardTicTacToe> player : players) {
            if (player instanceof OnlinePlayer<BitboardTicTacToe>){
                thread = new OnlineThreadBehaviour<>(game);
            }
        }
        super(new TicTacToeBitCanvas(), game, thread , "TicTacToe");
    }
}
