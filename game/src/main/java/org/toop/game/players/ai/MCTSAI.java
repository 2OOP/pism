package org.toop.game.players.ai;

import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.Random;

public class MCTSAI<T extends TurnBasedGame<T>> extends AbstractAI<T> {
	private static class Node {
		public TurnBasedGame<?> state;
		public long move;

		public Node parent;

		public int expanded;
		public Node[] children;

		public int visits;
		public float value;

		public Node(TurnBasedGame<?> state, long move, Node parent) {
			this.state = state;
			this.move = move;

			this.parent = parent;

			this.expanded = 0;
			this.children = new Node[Long.bitCount(state.getLegalMoves())];

			this.visits = 0;
			this.value = 0.0f;
		}

		public Node(TurnBasedGame<?> state) {
			this(state, 0L, null);
		}

		public boolean isFullyExpanded() {
			return expanded >= children.length;
		}

		public Node bestUCTChild(float explorationFactor) {
			int bestChildIndex = -1;
			float bestScore = Float.NEGATIVE_INFINITY;

			for (int i = 0; i < expanded; i++) {
				float exploitation = children[i].visits <= 0? 0 : children[i].value / children[i].visits;
				float exploration = explorationFactor * (float)(Math.sqrt(Math.log(visits) / (children[i].visits + 0.001f)));

				float score = exploitation + exploration;

				if (score > bestScore) {
					bestChildIndex = i;
					bestScore = score;
				}
			}

			return bestChildIndex >= 0? children[bestChildIndex] : this;
		}
	}

	private final int milliseconds;

	public MCTSAI(int milliseconds) {
		this.milliseconds = milliseconds;
	}

	public MCTSAI(MCTSAI<T> other) {
		this.milliseconds = other.milliseconds;
	}

	@Override
	public MCTSAI<T> deepCopy() {
		return new MCTSAI<>(this);
	}

	@Override
	public long getMove(T game) {
		Node root = new Node(game.deepCopy());

		long endTime = System.currentTimeMillis() + milliseconds;

		while (System.currentTimeMillis() <= endTime) {
			Node node = selection(root);
			long legalMoves = node.state.getLegalMoves();

			if (legalMoves != 0) {
				node = expansion(node, legalMoves);
			}

			float result = 0.0f;

			if (node.state.getLegalMoves() != 0) {
				result = simulation(node.state, game.getCurrentTurn());
			}

			backPropagation(node, result);
		}

		int mostVisitedIndex = -1;
		int mostVisits = -1;

		for (int i = 0; i < root.expanded; i++) {
			if (root.children[i].visits > mostVisits) {
				mostVisitedIndex = i;
				mostVisits = root.children[i].visits;
			}
		}

		System.out.println("Visit count: " + root.visits);

		return mostVisitedIndex != -1? root.children[mostVisitedIndex].move : randomSetBit(game.getLegalMoves());
	}

	private Node selection(Node node) {
		while (node.state.getLegalMoves() != 0L && node.isFullyExpanded()) {
			node = node.bestUCTChild(1.41f);
		}

		return node;
	}

	private Node expansion(Node node, long legalMoves) {
		for (int i = 0; i < node.expanded; i++) {
			legalMoves &= ~node.children[i].move;
		}

		if (legalMoves == 0L) {
			return node;
		}

		long move = randomSetBit(legalMoves);

		TurnBasedGame<?> copy = node.state.deepCopy();
		copy.play(move);

		Node newlyExpanded = new Node(copy, move, node);

		node.children[node.expanded] = newlyExpanded;
		node.expanded++;

		return newlyExpanded;
	}

	private float simulation(TurnBasedGame<?> state, int playerIndex) {
		TurnBasedGame<?> copy = state.deepCopy();
		long legalMoves = copy.getLegalMoves();
		PlayResult result = null;

		while (legalMoves != 0) {
			result = copy.play(randomSetBit(legalMoves));
			legalMoves = copy.getLegalMoves();
		}

		if (result.state() == GameState.WIN) {
			if (result.player() == playerIndex) {
				return 1.0f;
			}

			return -1.0f;
		}

		return -0.2f;
	}

	private void backPropagation(Node node, float value) {
		while (node != null) {
			node.visits++;
			node.value += value;
			node = node.parent;
		}
	}

	public static long randomSetBit(long value) {
		Random random = new Random();

		int count = Long.bitCount(value);
		int target = random.nextInt(count);

		while (true) {
			int bit = Long.numberOfTrailingZeros(value);
			if (target == 0) {
				return 1L << bit;
			}
			value &= value - 1;
			target--;
		}
	}
}