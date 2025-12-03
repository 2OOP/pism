package org.toop.game.tictactoe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.games.tictactoe.TicTacToeAIR;
import org.toop.game.games.tictactoe.TicTacToeR;

import static org.junit.jupiter.api.Assertions.*;

final class TicTacToeAIRTest {

    private final TicTacToeAIR ai = new TicTacToeAIR();

    // Helper: play multiple moves in sequence on a fresh board
    private TicTacToeR playSequence(int... moves) {
        TicTacToeR game = new TicTacToeR(new Player[2]);
        for (int move : moves) {
            game.play(move);
        }
        return game;
    }

    @Test
    @DisplayName("AI first move must choose a corner")
    void testFirstMoveIsCorner() {
        TicTacToeR game = new TicTacToeR(new Player[2]);
        int move = ai.getMove(game);

        assertTrue(
                move == 0 || move == 2 || move == 6 || move == 8,
                "AI should pick a corner as first move"
        );
    }

    @Test
    @DisplayName("AI doesn't make losing move in specific situation")
    void testWinningMove(){
        TicTacToeR game = playSequence(new int[] { 0,  4, 5, 3, 6, 1, 7});
        int move = ai.getMove(game);

        assertEquals(8, move);
    }


    @Test
    @DisplayName("AI takes immediate winning move")
    void testAiTakesWinningMove() {
        // X = AI, O = opponent
        // Board state (X to play):
        // X | X | .
        // O | O | .
        // . | . | .
        //
        // AI must play 2 (top-right) to win.
        TicTacToeR game = playSequence(
                0, 3,   // X, O
                1, 4    // X, O
        );

        int move = ai.getMove(game);
        assertEquals(2, move, "AI must take the winning move at index 2");
    }

    @Test
    @DisplayName("AI blocks opponent's winning move")
    void testAiBlocksOpponent() {
        // Opponent threatens to win:
        // X | . | .
        // O | O | .
        // . | . | X
        // O is about to win at index 5; AI must block it.

        TicTacToeR game = playSequence(
                0, 3,   // X, O
                8, 4    // X, O   (O threatens at 5)
        );

        int move = ai.getMove(game);
        assertEquals(5, move, "AI must block opponent at index 5");
    }

    @Test
    @DisplayName("AI returns -1 when no legal moves exist")
    void testNoMovesAvailable() {
        TicTacToeR full = new TicTacToeR(new Player[2]);
        // Fill board alternating
        for (int i = 0; i < 9; i++) full.play(i);

        int move = ai.getMove(full);
        assertEquals(-1, move, "AI should return -1 when board is full");
    }

    @Test
    @DisplayName("Minimax depth does not cause crashes and produces valid move")
    void testDepthStability() {
        TicTacToeR game = playSequence(0, 4); // Simple mid-game state
        int move = ai.getMove(game);

        assertTrue(move >= -1 && move <= 8, "AI must return a valid move index");
    }

    @Test
    @DisplayName("AI chooses the optimal forced draw move")
    void testForcedDrawScenario() {
        // Scenario where only one move avoids immediate loss:
        //
        // X | O | X
        // X | O | .
        // O | X | .
        //
        // Legal moves: 5, 8
        // Only move 5 avoids losing.
        TicTacToeR game = new TicTacToeR(new Player[2]);
        int[] moves = {0,1,2,4,3,6,7};  // Hard-coded board setup
        for (int m : moves) game.play(m);

        int move = ai.getMove(game);
        assertEquals(5, move, "AI must choose the only move that avoids losing");
    }
}
