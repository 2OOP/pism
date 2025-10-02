package org.toop.game;

public abstract class TurnBasedGame extends Game {
	public final int turns;

	protected int currentTurn;

	protected TurnBasedGame(int rowSize, int columnSize, int turns) {
		assert turns >= 2;

		super(rowSize, columnSize);
		this.turns = turns;
	}

	protected TurnBasedGame(TurnBasedGame other) {
		super(other);
		turns = other.turns;
		currentTurn = other.currentTurn;
	}

	protected void nextTurn() {
		currentTurn = (currentTurn + 1) % turns;
	}

	public int getCurrentTurn() { return currentTurn; }
}