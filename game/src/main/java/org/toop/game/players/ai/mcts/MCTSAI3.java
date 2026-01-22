package org.toop.game.players.ai.mcts;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.game.players.ai.MCTSAI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MCTSAI3 extends MCTSAI {
	private final int threads;
	private final ExecutorService threadPool;

	public MCTSAI3(int milliseconds) {
		threads = 8;
		threadPool = Executors.newFixedThreadPool(8);
		super(milliseconds);
	}

	public MCTSAI3(int milliseconds, int threads) {
		this.threads = threads;
		threadPool = Executors.newFixedThreadPool(threads);
		super(milliseconds);
	}

	public MCTSAI3(MCTSAI3 other) {
		threads = 8;
		threadPool = Executors.newFixedThreadPool(8);
		super(other);
	}

	@Override
	public MCTSAI3 deepCopy() {
		return new MCTSAI3(this);
	}

	@Override
	public long getMove(TurnBasedGame game) {
		final Node root = new Node(game.deepCopy(), null, 0L);

		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		for (int i = 0; i < threads; i++) {
			threadPool.submit(() -> iterate(root, endTime));
		}

		try {
			threadPool.awaitTermination(milliseconds, TimeUnit.MILLISECONDS);

			lastIterations = root.visits.get();

			final Node mostVisitedChild = mostVisitedChild(root);
			return mostVisitedChild.move;
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