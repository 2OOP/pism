package org.toop.game.players.ai;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;

public class MCTSAI1<T extends TurnBasedGame<T>> extends AbstractAI<T> {
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

			return exploitation + exploration;
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

	private static final Random random = new Random();

	private final int milliseconds;

	public MCTSAI1(int milliseconds) {
		this.milliseconds = milliseconds;
	}

	public MCTSAI1(MCTSAI1<T> other) {
		this.milliseconds = other.milliseconds;
	}

	@Override
	public MCTSAI1<T> deepCopy() {
		return new MCTSAI1<>(this);
	}

	@Override
	public long getMove(T game) {
		final Node root = new Node(game, null, 0L);

		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		while (System.nanoTime() < endTime) {
			Node leaf = selection(root);
			leaf = expansion(leaf);
			final float value = simulation(leaf);
			backPropagation(leaf, value);
		}

		final Node mostVisitedChild = mostVisitedChild(root);
		return mostVisitedChild.move;
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
			final long randomMove = randomSetBit(legalMoves);

			copiedState.play(randomMove);
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
		final int randomBitCount = random.nextInt(bitCount);

		for (int i = 0; i < randomBitCount; i++) {
			value &= value - 1;
		}

		return value & -value;
	}
}