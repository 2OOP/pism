package org.toop.app.gameControllers;

import org.toop.app.canvas.GameCanvas;
import org.toop.app.canvas.ReversiBitCanvas;
import org.toop.framework.gameFramework.model.game.SupportsOnlinePlay;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.gameThreads.LocalThreadBehaviour;
import org.toop.game.gameThreads.OnlineThreadBehaviour;
import org.toop.game.gameThreads.OnlineWithSleepThreadBehaviour;
import org.toop.game.games.reversi.BitboardReversi;
import org.toop.game.games.tictactoe.BitboardTicTacToe;
import org.toop.game.players.OnlinePlayer;

public class ReversiBitController extends GenericGameController<BitboardReversi> {
    public ReversiBitController(Player<BitboardReversi>[] players) {
        BitboardReversi game = new BitboardReversi(players);
        ThreadBehaviour thread = new LocalThreadBehaviour<>(game);
        for (Player<BitboardReversi> player : players) {
            if (player instanceof OnlinePlayer<BitboardReversi>){
                thread = new OnlineWithSleepThreadBehaviour<>(game);
            }
        }
        super(new ReversiBitCanvas(), game, thread, "Reversi");
    }
}
