package org.toop.game.players.ai;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MCTSAI5<T extends TurnBasedGame<T>> extends AbstractAI<T> {
	private static class Node {
		public TurnBasedGame<?> state;

		public long move;
		public long unexpandedMoves;

		public Node parent;

		public Node[] children;
		public int expanded;

		public float value;
		public int visits;

		public boolean solved;
		public float solvedValue;

		public float heuristic;

		public Node(TurnBasedGame<?> state, Node parent, long move) {
			final long legalMoves = state.getLegalMoves();

			this.state = state;

			this.move = move;
			this.unexpandedMoves = legalMoves;

			this.parent = parent;

			this.children = new Node[Long.bitCount(legalMoves)];
			this.expanded = 0;

			this.value = 0.0f;
			this.visits = 0;

			this.solved = false;
			this.solvedValue = 0.0f;

			this.heuristic = state.rateMove(move);
		}

		public Node(TurnBasedGame<?> state) {
			this(state, null, 0L);
		}

		public boolean isFullyExpanded() {
			return expanded == children.length;
		}

		public float calculateUCT(int parentVisits) {
			if (visits == 0) {
				return Float.POSITIVE_INFINITY;
			}

			final float exploitation = value / visits;
			final float exploration = 1.41f * (float)(Math.sqrt(Math.log(parentVisits) / visits));
			final float bias = heuristic / visits;

			return exploitation + exploration + bias;
		}

		public Node bestUCTChild() {
			Node highestUCTChild = null;
			float highestUCT = Float.NEGATIVE_INFINITY;

			for (int i = 0; i < expanded; i++) {
				final float childUCT = children[i].calculateUCT(visits);

				if (childUCT > highestUCT) {
					highestUCTChild = children[i];
					highestUCT = childUCT;
				}
			}

			return highestUCTChild;
		}
	}

	private static final ThreadLocal<Random> random = ThreadLocal.withInitial(Random::new);

	private final int milliseconds;
	private final int threads;

	private final Node[] threadRoots;

	public MCTSAI5(int milliseconds, int threads) {
		this.milliseconds = milliseconds;
		this.threads = threads;

		this.threadRoots = new Node[threads];
	}

	public MCTSAI5(MCTSAI5<T> other) {
		this.milliseconds = other.milliseconds;
		this.threads = other.threads;

		this.threadRoots = other.threadRoots;
	}

	@Override
	public MCTSAI5<T> deepCopy() {
		return new MCTSAI5<>(this);
	}

	@Override
	public long getMove(T game) {
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

				while (System.nanoTime() < endTime) {
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

			final Node mostVisitedChild = mostVisitedChild(root);
			final long move = mostVisitedChild.move;

			for (int i = 0; i < threads; i++) {
				threadRoots[i] = findChildByMove(threadRoots[i], move);
			}

			return move;
		} catch (Exception _) {
			final long legalMoves = game.getLegalMoves();
			return randomSetBit(legalMoves);
		}
	}

	private Node mostVisitedChild(Node root) {
		Node mostVisitedChild = null;
		int mostVisited = -1;

		for (int i = 0; i < root.expanded; i++) {
			if (root.children[i].visits > mostVisited) {
				mostVisitedChild = root.children[i];
				mostVisited = root.children[i].visits;
			}
		}

		return mostVisitedChild;
	}

	private Node findOrResetRoot(Node root, T game) {
		if (root == null) {
			return new Node(game.deepCopy());
		}

		if (areStatesEqual(root.state.getBoard(), game.getBoard())) {
			return root;
		}

		for (int i = 0; i < root.expanded; i++) {
			if (areStatesEqual(root.children[i].state.getBoard(), game.getBoard())) {
				root.children[i].parent = null;
				return root.children[i];
			}
		}

		return new Node(game.deepCopy());
	}

	private Node findChildByMove(Node root, long move) {
		for (int i = 0; i < root.expanded; i++) {
			if (root.children[i].move == move) {
				root.children[i].parent = null;
				return root.children[i];
			}
		}

		return null;
	}

	private boolean areStatesEqual(long[] state1, long[] state2) {
		if (state1.length != state2.length) {
			return false;
		}

		for (int i = 0; i < state1.length; i++) {
			if (state1[i] != state2[i]) {
				return false;
			}
		}

		return true;
	}

	private Node selection(Node root) {
		while (!root.solved && root.isFullyExpanded() && !root.state.isTerminal()) {
			root = root.bestUCTChild();
		}

		return root;
	}

	private Node expansion(Node leaf) {
		if (leaf.unexpandedMoves == 0L) {
			return leaf;
		}

		final long unexpandedMove = leaf.unexpandedMoves & -leaf.unexpandedMoves;

		final TurnBasedGame<?> copiedState = leaf.state.deepCopy();
		copiedState.play(unexpandedMove);

		final Node expandedChild = new Node(copiedState, leaf, unexpandedMove);

		leaf.children[leaf.expanded] = expandedChild;
		leaf.expanded++;

		leaf.unexpandedMoves &= ~unexpandedMove;

		return expandedChild;
	}

	private float simulation(Node leaf) {
		final TurnBasedGame<?> copiedState = leaf.state.deepCopy();
		final int playerIndex = 1 - copiedState.getCurrentTurn();

		while (!copiedState.isTerminal()) {
			final long legalMoves = copiedState.getLegalMoves();

			long move = 0L;

			if (random.get().nextFloat() > 0.9f) {
				move = copiedState.heuristicMove(legalMoves);
			} else {
				move = randomSetBit(legalMoves);
			}

			copiedState.play(move);
		}

		if (copiedState.getWinner() == playerIndex) {
			return 1.0f;
		}

		if (copiedState.getWinner() >= 0) {
			return -1.0f;
		}

		return 0.0f;
	}

	private void backPropagation(Node leaf, float value) {
		while (leaf != null) {
			leaf.value += value;
			leaf.visits++;

			if (!leaf.solved) {
				updateSolvedStatus(leaf);
			}

			value = -value;
			leaf = leaf.parent;
		}
	}

	private void updateSolvedStatus(Node node) {
		if (node.state.isTerminal()) {
			node.solved = true;

			final int winner = node.state.getWinner();
			final int mover = 1 - node.state.getCurrentTurn();

			node.solvedValue = winner == mover? 1.0f : winner == -1? 0.0f : -1.0f;

			return;
		}

		if (node.isFullyExpanded()) {
			boolean allChildrenSolved = true;
			boolean foundWinningMove = false;
			boolean foundDrawMove = false;

			for (final Node child : node.children) {
				if (child.solved) {
					if (child.solvedValue == -1.0f) {
						foundWinningMove = true;
						break;
					}

					if (child.solvedValue == 0.0f) {
						foundDrawMove = true;
					}
				} else {
					allChildrenSolved = false;
				}
			}

			if (foundWinningMove) {
				node.solved = true;
				node.solvedValue = 1.0f;
			} else if (allChildrenSolved) {
				node.solved = true;
				node.solvedValue = foundDrawMove? 0.0f : -1.0f;
			}
		}
	}

	private long randomSetBit(long value) {
		if (0L == value) {
			return 0;
		}

		final int bitCount = Long.bitCount(value);
		final int randomBitCount = random.get().nextInt(bitCount);

		for (int i = 0; i < randomBitCount; i++) {
			value &= value - 1;
		}

		return value & -value;
	}
}