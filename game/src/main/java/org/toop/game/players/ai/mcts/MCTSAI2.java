package org.toop.game.players.ai.mcts;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.game.players.ai.MCTSAI;

public class MCTSAI2 extends MCTSAI {
	private Node root;

	public MCTSAI2(int milliseconds) {
		super(milliseconds);

		this.root = null;
	}

	public MCTSAI2(MCTSAI2 other) {
		super(other);

		this.root = other.root;
	}

	@Override
	public MCTSAI2 deepCopy() {
		return new MCTSAI2(this);
	}

	@Override
	public long getMove(TurnBasedGame game) {
		root = findOrResetRoot(root, game);

		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		while (Float.isNaN(root.solved) && System.nanoTime() < endTime) {
			Node leaf = selection(root);
			leaf = expansion(leaf);
			final float value = simulation(leaf);
			backPropagation(leaf, value);
		}

		lastIterations = root.visits;

		final Node mostVisitedChild = mostVisitedChild(root);
		final long move = mostVisitedChild.move;

		root = findChildByMove(root, move);

		return move;
	}
}