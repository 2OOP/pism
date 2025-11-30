package org.toop.game.reversi;

import org.toop.framework.gameFramework.AIR;
import org.toop.game.AI;
import org.toop.game.records.Move;

import java.util.Arrays;
import java.util.Random;

public final class ReversiAIR extends AIR<ReversiR> {
	@Override
	public int findBestMove(ReversiR game, int depth) {
        int[] moves = game.getLegalMoves();
        if (moves.length == 0) return -1;

        System.out.println("Moves: " + Arrays.toString(moves));
        int inty = new Random().nextInt(0, moves.length);
        System.out.println(inty);
		return moves[inty];
	}
}
