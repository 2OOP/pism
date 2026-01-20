package org.toop.game.players.ai.mcts;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.game.players.ai.MCTSAI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MCTSAI4 extends MCTSAI {
	private final int threads;
	private final Node[] threadRoots;

	public MCTSAI4(int milliseconds, int threads) {
		super(milliseconds);

		this.threads = threads;
		this.threadRoots = new Node[threads];
	}

	public MCTSAI4(MCTSAI4 other) {
		super(other);

		this.threads = other.threads;
		this.threadRoots = other.threadRoots;
	}

	@Override
	public MCTSAI4 deepCopy() {
		return new MCTSAI4(this);
	}

	@Override
	public long getMove(TurnBasedGame game) {
		for (int i = 0; i < threads; i++) {
			threadRoots[i] = findOrResetRoot(threadRoots[i], game);
		}

		final ExecutorService pool = Executors.newFixedThreadPool(threads);
		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		final List<Callable<Node>> tasks = new ArrayList<>();

		for (int i = 0; i < threads; i++) {
			final int threadIndex = i;

			tasks.add(() -> {
				final Node localRoot = threadRoots[threadIndex];

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
			IO.println("V4: " + lastIterations);

			final Node mostVisitedChild = mostVisitedChild(root);
			final long move = mostVisitedChild.move;

			for (int i = 0; i < threads; i++) {
				threadRoots[i] = findChildByMove(threadRoots[i], move);
			}

			return move;
		} catch (Exception _) {
			lastIterations = 0;

			final long legalMoves = game.getLegalMoves();
			return randomSetBit(legalMoves);
		}
	}
}