package org.toop.game.players;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;


public class RandomAI<T extends TurnBasedGame<T>> extends AbstractAI<T> {

    public RandomAI() {
        super();
    }

    @Override
    public RandomAI<T> deepCopy() {
        return new RandomAI<T>();
    }

    @Override
    public long getMove(T game) {
        System.out.println("Getting move?");
        long legalMoves = game.getLegalMoves();
        int move = new Random().nextInt(Long.bitCount(legalMoves));
        System.out.println("Legal moves: " + Long.toBinaryString(legalMoves));
        System.out.println("Playing: " + Long.toBinaryString(nthBitIndex(legalMoves, move)));
        return nthBitIndex(legalMoves, move);
    }

    public static long nthBitIndex(long bb, int n) {
        while (bb != 0) {
            int tz = Long.numberOfTrailingZeros(bb);
            if (n == 0) {
                return 1L << tz;
            }
            bb &= bb - 1; // clear the least significant 1
            n--;
        }
        return 0L; // not enough 1s
    }
}
