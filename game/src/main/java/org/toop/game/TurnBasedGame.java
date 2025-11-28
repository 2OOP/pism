package org.toop.game;

public abstract class TurnBasedGame extends Game {
    private final int playerCount;  // How many players are playing
    private int turn = 0;           // What turn it is in the game

    protected TurnBasedGame(int rowSize, int columnSize, int playerCount) {
        super(rowSize, columnSize);
        this.playerCount = playerCount;
    }

    protected TurnBasedGame(TurnBasedGame other) {
        super(other);
        playerCount = other.playerCount;
        turn = other.turn;
    }

    protected void nextTurn() {
        turn += 1;
    }

    public int getCurrentTurn() {
        return turn % playerCount;
    }
}
