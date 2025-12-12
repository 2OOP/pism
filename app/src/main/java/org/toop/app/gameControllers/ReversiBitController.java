package org.toop.app.gameControllers;

import org.toop.app.canvas.ReversiBitCanvas;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.game.gameThreads.LocalThreadBehaviour;
import org.toop.framework.game.gameThreads.OnlineThreadBehaviour;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.game.players.OnlinePlayer;

public class ReversiBitController extends GenericGameController {
    public ReversiBitController(Player[] players) {
        BitboardReversi game = new BitboardReversi();
        game.init(players);
        ThreadBehaviour thread = new LocalThreadBehaviour(game);
        for (Player player : players) {
            if (player instanceof OnlinePlayer){
                thread = new OnlineThreadBehaviour(game);
            }
        }
        super(new ReversiBitCanvas(), game, thread, "Reversi");
    }
}
