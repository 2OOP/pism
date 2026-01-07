package org.toop.game.players.ai;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;

public class MCTSAI3 extends AbstractAI {
	private static class Node {
		public TurnBasedGame state;

		public long move;
		public long unexpandedMoves;

		public Node parent;

		public Node[] children;
		public int expanded;

		public float value;
		public int visits;

		public Node(TurnBasedGame state, Node parent, long move) {
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

		public Node(TurnBasedGame state) {
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

	private Node root;
	private final int milliseconds;

	public MCTSAI3(int milliseconds) {
		this.random = new Random();

		this.root = null;
		this.milliseconds = milliseconds;
	}

	public MCTSAI3(MCTSAI3 other) {
		this.random = other.random;

		this.root = other.root;
		this.milliseconds = other.milliseconds;
	}

	@Override
	public MCTSAI3 deepCopy() {
		return new MCTSAI3(this);
	}

	@Override
	public long getMove(TurnBasedGame game) {
		detectRoot(game);

		final long endTime = System.nanoTime() + milliseconds * 1_000_000L;

		while (System.nanoTime() < endTime) {
			Node leaf = selection(root);
			leaf = expansion(leaf);
			final float value = simulation(leaf);
			backPropagation(leaf, value);
		}

		final Node mostVisitedChild = mostVisitedChild(root);
		final long move = mostVisitedChild != null? mostVisitedChild.move : 0L;

		newRoot(move);

		return move;
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

	private void detectRoot(TurnBasedGame game) {
		if (root == null) {
			root = new Node(game.deepCopy());
			return;
		}

		final long[] currentBoards = game.getBoard();
		final long[] rootBoards = root.state.getBoard();

		boolean detected = true;

		for (int i = 0; i < rootBoards.length; i++) {
			if (rootBoards[i] != currentBoards[i]) {
				detected = false;
				break;
			}
		}

		if (detected) {
			return;
		}

		for (int i = 0; i < root.expanded; i++) {
			final Node child = root.children[i];

			final long[] childBoards = child.state.getBoard();

			detected = true;

			for (int j = 0; j < childBoards.length; j++) {
				if (childBoards[j] != currentBoards[j]) {
					detected = false;
					break;
				}
			}

			if (detected) {
				root = child;
				return;
			}
		}

		root = new Node(game.deepCopy());
	}

	private void newRoot(long move) {
		for (final Node child : root.children) {
			if (child.move == move) {
				root = child;
				break;
			}
		}
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

		final TurnBasedGame copiedState = leaf.state.deepCopy();
		copiedState.play(unexpandedMove);

		final Node expandedChild = new Node(copiedState, leaf, unexpandedMove);

		leaf.children[leaf.expanded] = expandedChild;
		leaf.expanded++;

		leaf.unexpandedMoves &= ~unexpandedMove;

		return expandedChild;
	}

	private float simulation(Node leaf) {
		final TurnBasedGame copiedState = leaf.state.deepCopy();
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
