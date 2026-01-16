package org.toop.game.players.ai.mcts;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.game.players.ai.MCTSAI;

public class MCTSAI1<T extends TurnBasedGame<T>> extends MCTSAI<T> {
	public MCTSAI1(int milliseconds) {
		super(milliseconds);
	}

	public MCTSAI1(MCTSAI1<T> other) {
		super(other);
	}

	@Override
	public MCTSAI1<T> deepCopy() {
		return new MCTSAI1<>(this);
	}

	@Override
	public long getMove(T game) {
		final Node root = new Node(game, null, 0L);

		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		// while (Float.isNaN(root.solved) && System.nanoTime() < endTime) {
		while (System.nanoTime() < endTime) {
			Node leaf = selection(root);
			leaf = expansion(leaf);
			final float value = simulation(leaf);
			backPropagation(leaf, value);
		}

		lastIterations = root.visits;

		final Node mostVisitedChild = mostVisitedChild(root);
		return mostVisitedChild.move;
	}
}