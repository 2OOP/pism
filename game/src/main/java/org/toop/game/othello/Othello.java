package org.toop.game.othello;

import org.toop.game.TurnBasedGame;

public final class Othello extends TurnBasedGame {
	Othello() {
		super(8, 8, 2);
	}

	@Override
	public Move[] getLegalMoves() {
		return new Move[0];
	}

	@Override
	public State play(Move move) {
		return null;
	}
}