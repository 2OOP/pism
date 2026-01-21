package org.toop.game.players.ai;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MCTSAI extends AbstractAI {
	protected static class Node {
		public static final int VIRTUAL_LOSS = -1;

		public TurnBasedGame state;

		public long move;
		public long unexpandedMoves;

		public Node parent;
		public Node[] children;

		public AtomicInteger value;
		public AtomicInteger visits;

		public float heuristic;

		public float solved;

		public Node(TurnBasedGame state, Node parent, long move) {
			final long legalMoves = state.getLegalMoves();

			this.state = state;

			this.move = move;
			this.unexpandedMoves = legalMoves;

			this.parent = parent;
			this.children = new Node[Long.bitCount(legalMoves)];

			this.value = new AtomicInteger(0);
			this.visits = new AtomicInteger(0);

			this.heuristic = state.rateMove(move);

			this.solved = Float.NaN;
		}

		public Node(TurnBasedGame state) {
			this(state, null, 0L);
		}

		public int getExpanded() {
			return children.length - Long.bitCount(unexpandedMoves);
		}

		public boolean isFullyExpanded() {
			return unexpandedMoves == 0L;
		}

		public float calculateUCT(float explorationFactor) {
			if (visits.get() == 0) {
				return Float.POSITIVE_INFINITY;
			}

			final float exploitation = (float) value.get() / visits.get();
			final float exploration = (float)(Math.sqrt(explorationFactor / visits.get()));
			final float bias = heuristic * 10.0f / (visits.get() + 1);

			return exploitation + exploration + bias;
		}

		public Node bestUCTChild() {
			final int expanded = getExpanded();

			Node highestUCTChild = null;
			float highestUCT = Float.NEGATIVE_INFINITY;

			for (int i = 0; i < expanded; i++) {
				final float childUCT = children[i].calculateUCT(2.0f * (float)Math.log(visits.get()));

				if (childUCT > highestUCT) {
					highestUCTChild = children[i];
					highestUCT = childUCT;
				}
			}

			return highestUCTChild;
		}
	}

	protected static final ThreadLocal<Random> random = ThreadLocal.withInitial(Random::new);

	protected final int milliseconds;

	protected int lastIterations;

	public MCTSAI(int milliseconds) {
		this.milliseconds = milliseconds;

		this.lastIterations = 0;
	}

	public MCTSAI(MCTSAI other) {
		this.milliseconds = other.milliseconds;

		this.lastIterations = other.lastIterations;
	}

	public int getLastIterations() {
		return lastIterations;
	}

	protected Node selection(Node root) {
		while (Float.isNaN(root.solved) && root.isFullyExpanded() && !root.state.isTerminal()) {
			root.value.addAndGet(Node.VIRTUAL_LOSS);
			root.visits.incrementAndGet();

			root = root.bestUCTChild();
		}

		root.value.addAndGet(Node.VIRTUAL_LOSS);
		root.visits.incrementAndGet();

		return root;
	}

	protected Node expansion(Node leaf) {
		synchronized (leaf) {
			if (leaf.unexpandedMoves == 0L) {
				return leaf;
			}

			final long unexpandedMove = leaf.unexpandedMoves & -leaf.unexpandedMoves;

			final TurnBasedGame copiedState = leaf.state.deepCopy();
			copiedState.play(unexpandedMove);

			final Node expandedChild = new Node(copiedState, leaf, unexpandedMove);

			leaf.children[leaf.getExpanded()] = expandedChild;
			leaf.unexpandedMoves &= ~unexpandedMove;

			return expandedChild;
		}
	}

	protected int simulation(Node leaf) {
		final TurnBasedGame copiedState = leaf.state.deepCopy();
		final int playerIndex = 1 - copiedState.getCurrentTurn();

		while (!copiedState.isTerminal()) {
			final long legalMoves = copiedState.getLegalMoves();
			final long randomMove = randomSetBit(legalMoves);

			copiedState.play(randomMove);
		}

		if (copiedState.getWinner() == playerIndex) {
			return 1;
		}

		if (copiedState.getWinner() >= 0) {
			return -1;
		}

		return 0;
	}

	protected void backPropagation(Node leaf, int value) {
		while (leaf != null) {
			value -= Node.VIRTUAL_LOSS;
			leaf.value.addAndGet(value);

			if (Float.isNaN(leaf.solved)) {
				updateSolvedStatus(leaf);
			}

			value = -value;
			leaf = leaf.parent;
		}
	}

	protected Node mostVisitedChild(Node root) {
		final int expanded = root.getExpanded();

		Node mostVisitedChild = null;
		int mostVisited = -1;

		for (int i = 0; i < expanded; i++) {
			if (root.children[i].visits.get() > mostVisited) {
				mostVisitedChild = root.children[i];
				mostVisited = root.children[i].visits.get();
			}
		}

		return mostVisitedChild;
	}

	protected Node findOrResetRoot(Node root, TurnBasedGame game) {
		if (root == null) {
			return new Node(game.deepCopy());
		}

		if (areStatesEqual(root.state.getBoard(), game.getBoard())) {
			return root;
		}

		final int expanded = root.getExpanded();

		for (int i = 0; i < expanded; i++) {
			if (areStatesEqual(root.children[i].state.getBoard(), game.getBoard())) {
				root.children[i].parent = null;
				return root.children[i];
			}
		}

		return new Node(game.deepCopy());
	}

	protected Node findChildByMove(Node root, long move) {
		final int expanded = root.getExpanded();

		for (int i = 0; i < expanded; i++) {
			if (root.children[i].move == move) {
				root.children[i].parent = null;
				return root.children[i];
			}
		}

		return null;
	}

	protected boolean areStatesEqual(long[] state1, long[] state2) {
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

	protected long randomSetBit(long value) {
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

	private void updateSolvedStatus(Node node) {
		if (node.state.isTerminal()) {
			final int winner = node.state.getWinner();
			final int mover = 1 - node.state.getCurrentTurn();

			node.solved = winner == mover? 1.0f : winner == -1? 0.0f : -1.0f;

			return;
		}

		if (node.isFullyExpanded()) {
			boolean allChildrenSolved = true;
			boolean foundWinningMove = false;
			boolean foundDrawMove = false;

			for (final Node child : node.children) {
				if (!Float.isNaN(child.solved)) {
					if (child.solved == -1.0f) {
						foundWinningMove = true;
						break;
					}

					if (child.solved == 0.0f) {
						foundDrawMove = true;
					}
				} else {
					allChildrenSolved = false;
				}
			}

			if (foundWinningMove) {
				node.solved = 1.0f;
			} else if (allChildrenSolved) {
				node.solved = foundDrawMove? 0.0f : -1.0f;
			}
		}
	}
}