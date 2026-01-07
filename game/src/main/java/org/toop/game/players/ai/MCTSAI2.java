package org.toop.game.players.ai;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;

public class MCTSAI2<T extends TurnBasedGame<T>> extends AbstractAI<T> {
	private static class Node {
		public TurnBasedGame<?> state;

		public long move;
		public long unexpandedMoves;

		public Node parent;

		public Node[] children;
		public int expanded;

		public float value;
		public int visits;

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
		}

		public Node(TurnBasedGame<?> state) {
			this(state, null, 0L);
		}

		public boolean isFullyExpanded() {
			return expanded == children.length;
		}

		public float calculateUCT(int parentVisits) {
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

	private final Random random;
	private final int milliseconds;

	public MCTSAI2(int milliseconds) {
		this.random = new Random();
		this.milliseconds = milliseconds;
	}

	public MCTSAI2(MCTSAI2<?> other) {
		this.random = other.random;
		this.milliseconds = other.milliseconds;
	}

	@Override
	public MCTSAI2<T> deepCopy() {
		return new MCTSAI2<>(this);
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

		return mostVisitedChild != null? mostVisitedChild.move : 0L;
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
		while (root.isFullyExpanded() && !root.state.isTerminal()) {
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
		} else if (copiedState.getWinner() >= 0) {
			return -1.0f;
		}

		return 0.0f;
	}

	private void backPropagation(Node leaf, float value) {
		while (leaf != null) {
			leaf.value += value;
			leaf.visits++;

			value = -value;
			leaf = leaf.parent;
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