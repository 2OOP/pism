package org.toop.game.reversi;

import org.toop.game.AI;
import org.toop.game.records.Move;

public final class ReversiAI extends AI<Reversi> {
	@Override
	public Move findBestMove(Reversi game, int depth) {
        Move[] moves = game.getLegalMoves();
        int inty = (int)(Math.random() * moves.length-.5f);
		return moves[inty];
	}
}
