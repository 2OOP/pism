package org.toop.game.players.ai.mcts;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.game.players.ai.MCTSAI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MCTSAI3 extends MCTSAI {
	private final int threads;

	public MCTSAI3(int milliseconds, int threads) {
		super(milliseconds);

		this.threads = threads;
	}

	public MCTSAI3(MCTSAI3 other) {
		super(other);

		this.threads = other.threads;
	}

	@Override
	public MCTSAI3 deepCopy() {
		return new MCTSAI3(this);
	}

	@Override
	public long getMove(TurnBasedGame game) {
		final ExecutorService pool = Executors.newFixedThreadPool(threads);
		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		final List<Callable<Node>> tasks = new ArrayList<>();

		for (int i = 0; i < threads; i++) {
			tasks.add(() -> {
				final Node localRoot = new Node(game.deepCopy());

				while (Float.isNaN(localRoot.solved) && System.nanoTime() < endTime) {
					Node leaf = selection(localRoot);
					leaf = expansion(leaf);
					final float value = simulation(leaf);
					backPropagation(leaf, value);
				}

				return localRoot;
			});
		}

		try {
			final List<Future<Node>> results = pool.invokeAll(tasks);

			pool.shutdown();

			final Node root = new Node(game.deepCopy());

			for (int i = 0; i < root.children.length; i++) {
				expansion(root);
			}

			for (final Future<Node> result : results) {
				final Node localRoot = result.get();

				for (final Node localChild : localRoot.children) {
					for (int i = 0; i < root.children.length; i++) {
						if (localChild.move == root.children[i].move) {
							root.children[i].visits += localChild.visits;
							root.visits += localChild.visits;
							break;
						}
					}
				}
			}

			lastIterations = root.visits;

			final Node mostVisitedChild = mostVisitedChild(root);
			return mostVisitedChild.move;
		} catch (Exception _) {
			lastIterations = 0;

			final long legalMoves = game.getLegalMoves();
			return randomSetBit(legalMoves);
		}
	}
}