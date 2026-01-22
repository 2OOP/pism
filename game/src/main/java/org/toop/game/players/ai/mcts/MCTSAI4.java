package org.toop.game.players.ai.mcts;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.game.players.ai.MCTSAI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MCTSAI4 extends MCTSAI {
	private final int threads;
	private final ExecutorService threadPool;

	private Node root;

	public MCTSAI4(int milliseconds) {
		threads = 8;
		threadPool = Executors.newFixedThreadPool(8);
		super(milliseconds);
		this.root = null;
	}

	public MCTSAI4(int milliseconds, int threads) {
		this.threads = threads;
		threadPool = Executors.newFixedThreadPool(threads);
		super(milliseconds);
		this.root = null;
	}

	public MCTSAI4(MCTSAI4 other) {
		threads = 8;
		threadPool = Executors.newFixedThreadPool(8);
		super(other);
		this.root = other.root;
	}

	@Override
	public MCTSAI4 deepCopy() {
		return new MCTSAI4(this);
	}

	@Override
	public long getMove(TurnBasedGame game) {
		root = findOrResetRoot(root, game);

		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		for (int i = 0; i < threads; i++) {
			threadPool.submit(() -> iterate(root, endTime));
		}

		try {
			threadPool.awaitTermination(milliseconds, TimeUnit.MILLISECONDS);

			lastIterations = root.visits.get();

			final Node mostVisitedChild = mostVisitedChild(root);
			final long move = mostVisitedChild.move;

			root = findChildByMove(root, move);

			return move;
		} catch (Exception _) {
			lastIterations = 0;

			final long legalMoves = game.getLegalMoves();
			return randomSetBit(legalMoves);
		}
	}

	private Void iterate(Node root, long endTime) {
		while (Float.isNaN(root.solved) && System.nanoTime() < endTime) {
			Node leaf = selection(root);
			leaf = expansion(leaf);
			final int value = simulation(leaf);
			backPropagation(leaf, value);
		}

		return null;
	}
}