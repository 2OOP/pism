package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gui.GUIEvents;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.TurnBasedGameR;
import org.toop.game.players.LocalPlayer;
import org.toop.game.players.Player;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ThreadBehaviourBase implements GameThreadStrategy{
    protected final AtomicBoolean isRunning = new AtomicBoolean();
    protected final TurnBasedGameR game;

    public ThreadBehaviourBase(TurnBasedGameR game) {
        this.game = game;
    }

    protected int getValidMove(Player player){
        // Get this player's valid moves
        int[] validMoves = game.getLegalMoves();
        // Make sure provided move is valid
        // TODO: Limit amount of retries?
        int move = player.getMove(game.clone());
        while (!contains(validMoves, move)) {
            move = player.getMove(game.clone());
        }
        return move;
    }

    // TODO: helper function, would like to replace to get rid of this method
    public static boolean contains(int[] array, int value){
        for (int i : array) if (i == value) return true;
        return false;
    }
}
