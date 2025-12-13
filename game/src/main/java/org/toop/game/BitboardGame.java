package org.toop.game;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

// There is AI performance to be gained by getting rid of non-primitives and thus speeding up deepCopy
public abstract class BitboardGame<T extends BitboardGame<T>> implements TurnBasedGame<T> {
	private final int columnSize;
	private final int rowSize;

    private Player<T>[] players;

	// long is 64 bits. Every game has a limit of 64 cells maximum.
	private final long[] playerBitboard;
	private int currentTurn = 0;

	public BitboardGame(int columnSize, int rowSize, int playerCount, Player<T>[] players) {
		this.columnSize = columnSize;
		this.rowSize = rowSize;
        this.players = players;
		this.playerBitboard = new long[playerCount];

		Arrays.fill(playerBitboard, 0L);
	}

	public BitboardGame(BitboardGame<T> other) {
		this.columnSize = other.columnSize;
		this.rowSize = other.rowSize;

		this.playerBitboard = other.playerBitboard.clone();
		this.currentTurn = other.currentTurn;
        this.players = Arrays.stream(other.players)
                .map(Player<T>::deepCopy)
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

    public Player<T> getPlayer(int index) {return players[index];}

	public int getCurrentPlayerIndex() {
        return currentTurn % playerBitboard.length;
	}

	public int getNextPlayer() {
		return (currentTurn + 1) % playerBitboard.length;
	}

    public Player<T> getCurrentPlayer(){
        return players[getCurrentPlayerIndex()];
    }



    @Override
    public long[] getBoard() {return this.playerBitboard;}

	public void nextTurn() {
        currentTurn++;
	}
}