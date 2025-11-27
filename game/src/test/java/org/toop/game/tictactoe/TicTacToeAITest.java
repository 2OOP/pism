package org.toop.game.tictactoe;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.game.records.Move;

class TicTacToeAITest {
    private TicTacToe game;
    private TicTacToeAI ai;

    @BeforeEach
    void setup() {
        game = new TicTacToe();
        ai = new TicTacToeAI();
    }

    @Test
    void testBestMove_returnWinningMoveWithDepth1() {
        // X X -
        // O O -
        // - - -
        game.play(new Move(0, 'X'));
        game.play(new Move(3, 'O'));
        game.play(new Move(1, 'X'));
        game.play(new Move(4, 'O'));

        final Move move = ai.findBestMove(game, 1);

        assertNotNull(move);
        assertEquals('X', move.value());
        assertEquals(2, move.position());
    }

    @Test
    void testBestMove_blockOpponentWinDepth1() {
        // - - -
        // O - -
        // X X -
        game.play(new Move(6, 'X'));
        game.play(new Move(3, 'O'));
        game.play(new Move(7, 'X'));

        final Move move = ai.findBestMove(game, 1);

        assertNotNull(move);
        assertEquals('O', move.value());
        assertEquals(8, move.position());
    }

    @Test
    void testBestMove_preferCornerOnEmpty() {
        final Move move = ai.findBestMove(game, 0);

        assertNotNull(move);
        assertEquals('X', move.value());
        assertTrue(Set.of(0, 2, 6, 8).contains(move.position()));
    }

    @Test
    void testBestMove_findBestMoveDraw() {
        // O X -
        // - O X
        // X O X
        game.play(new Move(1, 'X'));
        game.play(new Move(0, 'O'));
        game.play(new Move(5, 'X'));
        game.play(new Move(4, 'O'));
        game.play(new Move(6, 'X'));
        game.play(new Move(7, 'O'));
        game.play(new Move(8, 'X'));

        final Move move = ai.findBestMove(game, game.getLegalMoves().length);

        assertNotNull(move);
        assertEquals('O', move.value());
        assertEquals(2, move.position());
    }
}
