package org.toop.game.players.ai.mcts;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.game.players.ai.MCTSAI;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MCTSAI3 extends MCTSAI {
	private final int threads;
	private final ExecutorService threadPool;

	public MCTSAI3(int milliseconds, int threads) {
		super(milliseconds);

		this.threads = threads;
		this.threadPool = Executors.newFixedThreadPool(threads);
	}

	public MCTSAI3(MCTSAI3 other) {
		super(other);

		this.threads = other.threads;
		this.threadPool = other.threadPool;
	}

	@Override
	public MCTSAI3 deepCopy() {
		return new MCTSAI3(this);
	}

	@Override
	public long getMove(TurnBasedGame game) {
		final Node root = new Node(game.deepCopy(), null, 0L);

		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		final CountDownLatch latch = new CountDownLatch(threads);

		for (int i = 0; i < threads; i++) {
			threadPool.submit(() -> {
				try {
					iterate(root, endTime);
				} finally {
					latch.countDown();
				}
			});
		}

		try {
			final long remaining = endTime - System.nanoTime();
			latch.await(remaining, TimeUnit.NANOSECONDS);

			lastIterations = root.visits.get();

			final Node mostVisitedChild = mostVisitedChild(root);
			return mostVisitedChild.move;
		} catch (Exception _) {
			lastIterations = 0;

			final long legalMoves = game.getLegalMoves();
			return randomSetBit(legalMoves);
		}
	}

	private void iterate(Node root, long endTime) {
		while (Float.isNaN(root.solved) && System.nanoTime() < endTime) {
			Node leaf = selection(root);
			leaf = expansion(leaf);
			final int value = simulation(leaf);
			backPropagation(leaf, value);
		}
	}
}