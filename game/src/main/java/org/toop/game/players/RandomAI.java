package org.toop.game.players;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;


public class RandomAI extends AbstractAI {

    public RandomAI() {
        super();
    }

    @Override
    public RandomAI deepCopy() {
        return new RandomAI();
    }

    @Override
    public long getMove(TurnBasedGame game) {
        long legalMoves = game.getLegalMoves();
        int move = new Random().nextInt(Long.bitCount(legalMoves));
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
