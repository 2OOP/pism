package org.toop.game.reversi;

import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;

public final class ReversiAIR extends AbstractAI<ReversiR> {
	public int getMove(ReversiR game) {
        int[] moves = game.getLegalMoves();
        if (moves.length == 0) return -1;

        int inty = new Random().nextInt(0, moves.length);
		return moves[inty];
	}
}
