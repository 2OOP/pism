package org.toop.game;

import org.toop.game.records.Move;

public abstract class AI<T extends Game> {
	public abstract Move findBestMove(T game, int depth);
}