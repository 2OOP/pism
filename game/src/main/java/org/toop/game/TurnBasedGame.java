package org.toop.game;

public abstract class TurnBasedGame extends Game {
    private final int turns;
    private int currentTurn;

    protected TurnBasedGame(int rowSize, int columnSize, int turns) {
        super(rowSize, columnSize);
        assert turns >= 2;
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

    public int getCurrentTurn() {
        return currentTurn;
    }
}
