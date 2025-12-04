package org.toop.game.games.reversi;

import org.toop.framework.gameFramework.model.player.AI;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;

public final class ReversiAIR extends AbstractAI<BitboardReversi>{
	public int getMove(BitboardReversi game) {
        int[] moves = game.getLegalMoves();
        if (moves.length == 0) return -1;

        int inty = new Random().nextInt(0, moves.length);
		return moves[inty];
	}

    public ReversiAIR deepCopy() {
        return new ReversiAIR();
    }
}
