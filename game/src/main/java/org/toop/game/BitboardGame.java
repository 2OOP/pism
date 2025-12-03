package org.toop.game;

import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BitboardGame implements TurnBasedGame<BitboardGame> {
	private final int columnSize;
	private final int rowSize;

	// long is 64 bits. Every game has a limit of 64 cells maximum.
	private final long[] playerBitboard;
	private int currentTurn;

	public BitboardGame(int columnSize, int rowSize, int playerCount) {
		this.columnSize = columnSize;
		this.rowSize = rowSize;

		this.playerBitboard = new long[playerCount];
		this.currentTurn = 0;

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
        return output;
    }

    protected long translateMove(int move){
        return 1L << move;
    }

    protected int[] translateBoard(){
        int[] output = new int[64];
        for(int i = 0; i < this.playerBitboard.length; i++){
            for (int j = 0; j < 64; j++){
                if ((this.playerBitboard[i] & (1L << j)) != 0){
                    output[j] = i;
                }
            }
        }
        return output;
    }

	public BitboardGame(BitboardGame other) {
		this.columnSize = other.columnSize;
		this.rowSize = other.rowSize;

		this.playerBitboard = Arrays.copyOf(other.playerBitboard, other.playerBitboard.length);
		this.currentTurn = other.currentTurn;
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
		return currentTurn;
	}

	public int getCurrentPlayer() {
		return currentTurn % playerBitboard.length;
	}

	public int getNextPlayer() {
		return (currentTurn + 1) % playerBitboard.length;
	}

	public void nextTurn() {
		currentTurn++;
	}
}