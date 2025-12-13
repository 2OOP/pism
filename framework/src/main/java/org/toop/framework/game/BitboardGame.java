package org.toop.framework.game;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.Arrays;

// There is AI performance to be gained by getting rid of non-primitives and thus speeding up deepCopy
public abstract class BitboardGame implements TurnBasedGame {
	private final int columnSize;
	private final int rowSize;

    private Player[] players;

	// long is 64 bits. Every game has a limit of 64 cells maximum.
	private final long[] playerBitboard;
	private int currentTurn = 0;
	private final int playerCount;

	public BitboardGame(int columnSize, int rowSize, int playerCount) {
		this.columnSize = columnSize;
		this.rowSize = rowSize;
		this.playerCount = playerCount;
        this.playerBitboard = new long[playerCount];
	}

	@Override
	public void init(Player[] players) {
		this.players = players;

		Arrays.fill(playerBitboard, 0L);
	}

	public BitboardGame(BitboardGame other) {
		this.columnSize = other.columnSize;
		this.rowSize = other.rowSize;
        this.playerCount = other.playerCount;
		this.playerBitboard = other.playerBitboard.clone();
		this.currentTurn = other.currentTurn;
        this.players = Arrays.stream(other.players)
                .map(Player::deepCopy)
                .toArray(Player[]::new);
	}

	public int getColumnSize() {
		return this.columnSize;
	}

	public int getRowSize() {
		return this.rowSize;
	}

	public long getPlayerBitboard(int player) {
		return this.playerBitboard[player];
	}

	public void setPlayerBitboard(int player, long bitboard) {
		this.playerBitboard[player] = bitboard;
	}

	public int getPlayerCount() {
		return playerBitboard.length;
	}

	public int getCurrentTurn() {
		return getCurrentPlayerIndex();
	}

    public Player getPlayer(int index) {return players[index];}

	public int getCurrentPlayerIndex() {
        return currentTurn % playerBitboard.length;
	}

	public int getNextPlayer() {
		return (currentTurn + 1) % playerBitboard.length;
	}

    public Player getCurrentPlayer(){
        return players[getCurrentPlayerIndex()];
    }



    @Override
    public long[] getBoard() {return this.playerBitboard;}

	public void nextTurn() {
        currentTurn++;
	}
}