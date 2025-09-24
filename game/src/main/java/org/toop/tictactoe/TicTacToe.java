package org.toop.tictactoe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.game.GameBase;
import org.toop.game.Player;

// Todo: refactor
public class TicTacToe extends GameBase {

    protected static final Logger logger = LogManager.getLogger(TicTacToe.class);

    public Thread gameThread;
    public String gameId;

    public int movesLeft;

    public TicTacToe(String player1, String player2) {
        super(3, new Player(player1, 'X'), new Player(player2, 'O'));
        movesLeft = size * size;
    }

    /**
     * Used for the server.
     *
     * @param player1
     * @param player2
     * @param gameId
     */
    public TicTacToe(String player1, String player2, String gameId) {
        super(3, new Player(player1, 'X'), new Player(player2, 'O'));
        this.gameId = gameId;
        movesLeft = size * size;
    }

    @Override
    public State play(int index) {
        if (!validateMove(index)) {
            return State.INVALID;
        }

        grid[index] = getCurrentPlayer().getSymbol();
        movesLeft--;

        if (checkWin()) {
            return State.WIN;
        }

        if (movesLeft <= 0) {
            return State.DRAW;
        }

        currentPlayer = (currentPlayer + 1) % players.length;
        return State.NORMAL;
    }

    public boolean validateMove(int index) {
        return movesLeft > 0 && isInside(index) && grid[index] == EMPTY;
    }

    public boolean checkWin() {
        // Horizontal
        for (int i = 0; i < 3; i++) {
            final int index = i * 3;

            if (grid[index] != EMPTY
                    && grid[index] == grid[index + 1]
                    && grid[index] == grid[index + 2]) {
                return true;
            }
        }

        // Vertical
        for (int i = 0; i < 3; i++) {
            int index = i;

            if (grid[index] != EMPTY
                    && grid[index] == grid[index + 3]
                    && grid[index] == grid[index + 6]) {
                return true;
            }
        }

        // B-Slash
        if (grid[0] != EMPTY && grid[0] == grid[4] && grid[0] == grid[8]) {
            return true;
        }

        // F-Slash
        if (grid[2] != EMPTY && grid[2] == grid[4] && grid[2] == grid[6]) {
            return true;
        }

        return false;
    }

    /** For AI use only. */
    public void decrementMovesLeft() {
        movesLeft--;
    }

    /** This method copies the board, mainly for AI use. */
    public TicTacToe copyBoard() {
        TicTacToe clone = new TicTacToe(players[0].getName(), players[1].getName());
        System.arraycopy(this.grid, 0, clone.grid, 0, this.grid.length);
        clone.movesLeft = this.movesLeft;
        clone.currentPlayer = this.currentPlayer;
        return clone;
    }
}
