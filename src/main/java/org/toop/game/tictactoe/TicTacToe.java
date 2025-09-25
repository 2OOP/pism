package org.toop.game.tictactoe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.backend.tictactoe.ParsedCommand;
import org.toop.backend.tictactoe.TicTacToeServerCommand;

public class TicTacToe extends GameBase implements Runnable {

    protected static final Logger logger = LogManager.getLogger(TicTacToe.class);

    public Thread gameThread;
    public String gameId;
    public BlockingQueue<ParsedCommand> commandQueue = new LinkedBlockingQueue<>();
    public BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();

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

    public void addCommandToQueue(ParsedCommand command) {
        commandQueue.add(command);
    }

    private ParsedCommand takeFromCommandQueue() {
        try {
            return this.commandQueue.take();
        } catch (InterruptedException e) {
            logger.error("Taking from queue interrupted, in game with id: {}", this.gameId);
            return null;
        }
    }

    private void addSendToQueue(String send) {
        try {
            sendQueue.put(send);
        } catch (InterruptedException e) {
            logger.error("Sending to queue interrupted, in game with id: {}", this.gameId);
        }
    }

    @Override
    public void run() {
        this.gameThread = new Thread(this::gameThread);
        this.gameThread.start();
    }

    private void gameThread() {
        boolean running = true;

        while (running) {
            ParsedCommand cmd = takeFromCommandQueue();

            // Get next command if there was no command
            if (cmd == null) {
                continue;
            }

            // Do something based which command was given
            switch (cmd.command) {
                case TicTacToeServerCommand.MOVE:
                    {
                        // TODO: Check if it is this player's turn, not required for local play (I
                        // think?).

                        // Convert given argument to integer
                        Object arg = cmd.arguments.getFirst();
                        int index;
                        try {
                            index = Integer.parseInt((String) arg);
                        } catch (Exception e) {
                            logger.error("Error parsing argument to String or Integer");
                            continue;
                        }

                        // Attempt to play the move
                        State state = play(index);

                        if (state != State.INVALID) {
                            Player[] players = getPlayers();
                            Player player1 = players[0];
                            Player player2 = players[1];

                            char symbol1 = player1.getSymbol();
                            char symbol2 = player2.getSymbol();

                            char[] grid = getGrid();

                            boolean result = checkDraw(symbol1, symbol2, grid);

                            if (result) {
                                state = State.DRAW;
                            }
                        }

                        if (state != State.INVALID) {
                            // Tell all players who made a move and what move was made
                            // TODO: What is the reaction of the game? WIN, DRAW etc?
                            String player = getCurrentPlayer().getName();
                            addSendToQueue(
                                    "SVR GAME MOVE {PLAYER: \""
                                            + player
                                            + "\", DETAILS: \"<reactie spel op zet>\",MOVE: \""
                                            + index
                                            + "\"}\n");
                        }

                        // Check move result
                        switch (state) {
                            case State.WIN:
                                {
                                    // Win
                                    running = false;
                                    addSendToQueue(
                                            "SVR GAME WIN {PLAYERONESCORE: \"<score speler1>\","
                                                + " PLAYERTWOSCORE: \"<score speler2>\", COMMENT:"
                                                + " \"<commentaar op resultaat>\"}\n");
                                    break;
                                }
                            case State.DRAW:
                                {
                                    // Draw
                                    running = false;
                                    addSendToQueue(
                                            "SVR GAME DRAW {PLAYERONESCORE: \"<score speler1>\","
                                                + " PLAYERTWOSCORE: \"<score speler2>\", COMMENT:"
                                                + " \"<commentaar op resultaat>\"}\n");
                                    break;
                                }
                            case State.NORMAL:
                                {
                                    // Valid move but not end of game
                                    addSendToQueue("SVR GAME YOURTURN");
                                    break;
                                }
                            case State.INVALID:
                                {
                                    // Invalid move
                                    break;
                                }
                        }
                    }
            }
        }
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

    /** This method checks if there is a guaranteed draw through draw conditions. The code speaks for itself, even if it is a bit long. */

    public boolean checkDraw(char symbol1, char symbol2, char[] grid) {


        if (movesLeft > 2) { // always winnable up to 7 moves played
            return false;
        }

        // Horizontal check if the line is winnable

        for (int i = 0; i < 3; i++) {
            final int index = i * 3;


            if (grid[index] != EMPTY && grid[index + 1] != EMPTY && grid[index + 2] == EMPTY) {
                char horizontalIndex1 = grid[index];
                char horizontalIndex2 = grid[index + 1];

                if (horizontalIndex1 == horizontalIndex2) {
                    return false;
                }
            }

            if (grid[index] != EMPTY && grid[index + 1] == EMPTY && grid[index + 2] != EMPTY) {
                char horizontalIndex1 = grid[index];
                char horizontalIndex2 = grid[index + 2];

                if (horizontalIndex1 == horizontalIndex2) {
                    return false;
                }
            }

            if (grid[index] == EMPTY && grid[index + 1] != EMPTY && grid[index + 2] != EMPTY) {
                char horizontalIndex1 = grid[index + 1];
                char horizontalIndex2 = grid[index + 2];

                if (horizontalIndex1 == horizontalIndex2) {
                    return false;
                }
            }
        }
        // Vertical check if the line is winnable
        for (int i = 0; i < 3; i++) {
            int index = i;


            if (grid[index] != EMPTY && grid[index + 3] != EMPTY && grid[index + 6] == EMPTY) {
                char verticalIndex1 = grid[index];
                char verticalIndex2 = grid[index + 3];

                if (verticalIndex1 == verticalIndex2) {
                    return false;
                }
            }

            if (grid[index] != EMPTY && grid[index + 3] == EMPTY && grid[index + 6] != EMPTY) {
                char verticalIndex1 = grid[index];
                char verticalIndex2 = grid[index + 6];

                if (verticalIndex1 == verticalIndex2) {
                    return false;
                }
            }

            if (grid[index] == EMPTY && grid[index + 3] != EMPTY && grid[index + 6] != EMPTY) {
                char verticalIndex1 = grid[index + 3];
                char verticalIndex2 = grid[index + 6];

                if (verticalIndex1 == verticalIndex2) {
                    return false;
                }
            }
        }
        // B-Slash check if the line is winnable (0, 4, 8)


        if (grid[0] != EMPTY && grid[4] != EMPTY && grid[8] == EMPTY) {
            char diagonalIndex1 = grid[0];
            char diagonalIndex2 = grid[4];

            if (diagonalIndex1 == diagonalIndex2) {
                return false;
            }
        }

        if (grid[0] != EMPTY && grid[4] == EMPTY && grid[8] != EMPTY) {
            char diagonalIndex1 = grid[0];
            char diagonalIndex2 = grid[8];

            if (diagonalIndex1 == diagonalIndex2) {
                return false;
            }
        }

        if (grid[0] == EMPTY && grid[4] != EMPTY && grid[8] != EMPTY) {
            char diagonalIndex1 = grid[4];
            char diagonalIndex2 = grid[8];

            if (diagonalIndex1 == diagonalIndex2) {
                return false;
            }
        }

        // F-Slash check if the line is winnable (2, 4, 6)


        if (grid[2] != EMPTY && grid[4] != EMPTY && grid[6] == EMPTY) {
            char diagonalIndex1 = grid[2];
            char diagonalIndex2 = grid[4];

            if (diagonalIndex1 == diagonalIndex2) {
                return false;
            }
        }

        if (grid[2] != EMPTY && grid[4] == EMPTY && grid[6] != EMPTY) {
            char diagonalIndex1 = grid[2];
            char diagonalIndex2 = grid[6];

            if (diagonalIndex1 == diagonalIndex2) {
                return false;
            }
        }

        if (grid[2] == EMPTY && grid[4] != EMPTY && grid[6] != EMPTY) {
            char diagonalIndex1 = grid[4];
            char diagonalIndex2 = grid[6];

            if (diagonalIndex1 == diagonalIndex2) {
                return false;
            }
        }
        return true;
    }
}
