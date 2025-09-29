package org.toop.game;

public abstract class AI<T extends Game> {
	public abstract Game.Move findBestMove(T game, int depth);
}