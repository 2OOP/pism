package org.toop.game.tictactoe;

import org.toop.game.Game;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.game.reversi.Reversi;
import org.toop.game.reversi.ReversiAI;

import static org.junit.jupiter.api.Assertions.*;

class ReversiTest {
    private Reversi game;
    private ReversiAI ai;

    @BeforeEach
    void setup() {
        game = new Reversi();
        ai = new ReversiAI();
    }


    @Test
    void testCorrectStartPiecesPlaced() {
        assertNotNull(game);
        assertEquals('W',game.board[27]);
        assertEquals('B',game.board[28]);
        assertEquals('B',game.board[35]);
        assertEquals('W',game.board[36]);
    }
    @Test
    void testGetLegalMovesAtStart() {
        Game.Move[] moves = game.getLegalMoves();
        assertNotNull(moves);
        assertTrue(moves.length > 0);
        assertEquals(new Game.Move(19,'B'),moves[0]);
        assertEquals(new Game.Move(26,'B'),moves[0]);
        assertEquals(new Game.Move(37,'B'),moves[0]);
        assertEquals(new Game.Move(44,'B'),moves[0]);
    }
    @Test
    void testMakeValidMoveFlipsPieces() {
        game.play(new Game.Move(19, 'B'));
        assertEquals('B', game.board[19]);
        assertEquals('B', game.board[27], "Piece should have flipped to B");
    }
    @Test
    void testMakeInvalidMoveDoesNothing() {
        char[] before = game.board.clone();
        game.play(new Game.Move(0, 'B'));
        assertArrayEquals(before, game.board, "Board should not change on invalid move");
    }
    @Test
    void testTurnSwitchesAfterValidMove() {
        char current = game.getCurrentPlayer();
        game.play(game.getLegalMoves()[0]);
        assertNotEquals(current, game.getCurrentPlayer(), "Player turn should switch after a valid move");
    }
    @Test
    void testCountScoreCorrectly() {
        Game.Score score = game.getScore();
        assertEquals(2, score.player1Score()); // Black
        assertEquals(2, score.player2Score()); // White
    }
    @Test
    void testAISelectsLegalMove() {
        Game.Move move = ai.findBestMove(game,4);
        assertNotNull(move);
        assertTrue(containsMove(game.getLegalMoves(),move), "AI should always choose a legal move");
    }
    private boolean containsMove(Game.Move[] moves, Game.Move move) {
        for (Game.Move m : moves) {
            if (m.equals(move)) return true;
        }
        return false;
    }
}
