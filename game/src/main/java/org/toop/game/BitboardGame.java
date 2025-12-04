package org.toop.game;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BitboardGame<T extends BitboardGame<T>> implements TurnBasedGame<T> {
	private final int columnSize;
	private final int rowSize;

    private Player<T>[] players;

	// long is 64 bits. Every game has a limit of 64 cells maximum.
	private final long[] playerBitboard;
	private AtomicInteger currentTurn = new AtomicInteger(0);

	public BitboardGame(int columnSize, int rowSize, int playerCount, Player<T>[] players) {
		this.columnSize = columnSize;
		this.rowSize = rowSize;
        this.players = players;
		this.playerBitboard = new long[playerCount];

		Arrays.fill(playerBitboard, 0L);
	}

    protected int[] translateLegalMoves(long legalMoves){
        int[] output = new int[Long.bitCount(legalMoves)];
        int j = 0;
        for(int i = 0; i < 64; i++){
            if ((legalMoves & (1L << i)) != 0){
                output[j] = i;
                j++;
            }
        }
        System.out.println(Arrays.toString(output));
        return output;
    }

    protected long translateMove(int move){
        return 1L << move;
    }

    protected int[] translateBoard(){
        int[] output = new int[64];
        Arrays.fill(output, -1);
        for(int i = 0; i < this.playerBitboard.length; i++){
            for (int j = 0; j < 64; j++){
                if ((this.playerBitboard[i] & (1L << j)) != 0){
                    output[j] = i;
                }
            }
        }
        return output;
    }

	public BitboardGame(BitboardGame<T> other) {
		this.columnSize = other.columnSize;
		this.rowSize = other.rowSize;

		this.playerBitboard = Arrays.copyOf(other.playerBitboard, other.playerBitboard.length);
		this.currentTurn = other.currentTurn;
        this.players = Arrays.copyOf(other.players, other.players.length); // TODO: Make this a deep copy
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
        System.out.println(currentTurn.get() % playerBitboard.length);
        return currentTurn.get() % playerBitboard.length;
	}

	public int getNextPlayer() {
		return (currentTurn.get() + 1) % playerBitboard.length;
	}

    public Player<T> getCurrentPlayer(){
        return players[getCurrentPlayerIndex()];
    }

	public void nextTurn() {
        System.out.println("Incrementing turn");
        currentTurn.incrementAndGet();
	}
}