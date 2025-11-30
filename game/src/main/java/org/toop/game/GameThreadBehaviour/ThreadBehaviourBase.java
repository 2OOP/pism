package org.toop.game.GameThreadBehaviour;

import org.toop.framework.gameFramework.TurnBasedGameR;
import org.toop.game.players.AbstractPlayer;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ThreadBehaviourBase implements GameThreadStrategy{
    protected final AtomicBoolean isRunning = new AtomicBoolean();
    protected final TurnBasedGameR game;

    public ThreadBehaviourBase(TurnBasedGameR game) {
        this.game = game;
    }

    protected int getValidMove(AbstractPlayer player){
        // Get this player's valid moves
        int[] validMoves = game.getLegalMoves();
        // Make sure provided move is valid
        // TODO: Limit amount of retries?
        int move = player.getMove(game.clone());
        while (!contains(validMoves, move)) {
            System.out.println("Not a valid move, try again");
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
