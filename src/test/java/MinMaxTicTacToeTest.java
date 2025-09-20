import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.game.GameBase;
import org.toop.game.tictactoe.MinMaxTicTacToe;
import org.toop.game.tictactoe.TicTacToe;

/** Unit tests for MinMaxTicTacToe AI. */
public class MinMaxTicTacToeTest {

    private MinMaxTicTacToe ai;
    private TicTacToe game;

    @BeforeEach // called before every test is done to make it work
    void setUp() {
        ai = new MinMaxTicTacToe();
        game = new TicTacToe("AI", "Human");
    }

    @Test
    void testBestMoveWinningMoveAvailable() {
        // Setup board where AI can win immediately
        // X = AI, O = player
        // X | X | .
        // O | O | .
        // . | . | .
        game.grid =
                new char[] {
                    'X',
                    'X',
                    GameBase.EMPTY,
                    'O',
                    'O',
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY
                };
        game.movesLeft = 4;

        int bestMove = ai.findBestMove(game);

        // Ai is expected to place at index 2 to win
        assertEquals(2, bestMove);
    }

    @Test
    void testBestMoveBlocksOpponentWin() {
        // Setup board where player could win next turn
        // O | O | .
        // X | . | .
        // . | . | .
        game.grid =
                new char[] {
                    'O',
                    'O',
                    GameBase.EMPTY,
                    'X',
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY
                };

        int bestMove = ai.findBestMove(game);

        // AI block at index 2 to continue the game
        assertEquals(2, bestMove);
    }

    @Test
    void testBestMoveCenterPreferredOnEmptyBoard() {
        // On empty board, center (index 4) is strongest
        int bestMove = ai.findBestMove(game);

        assertEquals(4, bestMove);
    }

    @Test
    void testDoMinimaxScoresWinPositive() {
        // Simulate a game state where AI has already won
        TicTacToe copy = game.copyBoard();
        copy.grid =
                new char[] {
                    'X',
                    'X',
                    'X',
                    'O',
                    'O',
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY
                };

        int score = ai.doMinimax(copy, 5, false);

        assertTrue(score > 0, "AI win should yield positive score");
    }

    @Test
    void testDoMinimaxScoresLossNegative() {
        // Simulate a game state where human has already won
        TicTacToe copy = game.copyBoard();
        copy.grid =
                new char[] {
                    'O',
                    'O',
                    'O',
                    'X',
                    'X',
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY,
                    GameBase.EMPTY
                };

        int score = ai.doMinimax(copy, 5, true);

        assertTrue(score < 0, "Human win should yield negative score");
    }

    @Test
    void testDoMinimaxDrawReturnsZero() {
        // Simulate a draw position
        TicTacToe copy = game.copyBoard();
        copy.grid =
                new char[] {
                    'X', 'O', 'X',
                    'X', 'O', 'O',
                    'O', 'X', 'X'
                };

        int score = ai.doMinimax(copy, 0, true);

        assertEquals(0, score, "Draw should return 0 score");
    }
}
