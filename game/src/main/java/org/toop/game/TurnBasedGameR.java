package org.toop.game;

public abstract class TurnBasedGameR extends GameR {
    private final int playerCount;  // How many players are playing
    private int turn = 0;           // What turn it is in the game

    protected TurnBasedGameR(int rowSize, int columnSize, int playerCount) {
        super(rowSize, columnSize);
        this.playerCount = playerCount;
    }

    protected TurnBasedGameR(TurnBasedGameR other){
        super(other);
        this.playerCount = other.playerCount;
        this.turn = other.turn;
    }

    public int getPlayerCount(){return this.playerCount;}

    protected void nextTurn() {
        turn += 1;
    }

    public int getCurrentTurn() {
        return turn % playerCount;
    }

    protected void setBoard(int position) {
        super.setBoardPosition(position, getCurrentTurn());
    }

    @Override
    public abstract TurnBasedGameR clone();
}
