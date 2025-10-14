package org.toop.game.tictactoe;

import org.toop.game.Game;

import java.util.*;

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
        List<Game.Move> expectedMoves = List.of(
                new Game.Move(19,'B'),
                new Game.Move(26,'B'),
                new Game.Move(37,'B'),
                new Game.Move(44,'B')
        );
        assertNotNull(moves);
        assertTrue(moves.length > 0);
        assertMovesMatchIgnoreOrder(expectedMoves, Arrays.asList(moves));
    }

    private void assertMovesMatchIgnoreOrder(List<Game.Move> expected, List<Game.Move> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertTrue(actual.contains(expected.get(i)));
            assertTrue(expected.contains(actual.get(i)));
        }
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
    void testCountScoreCorrectlyAtStart() {
        long start =  System.nanoTime();
        Game.Score score = game.getScore();
        assertEquals(2, score.player1Score()); // Black
        assertEquals(2, score.player2Score()); // White
        long end  =  System.nanoTime();
        IO.println((end-start));
    }

    @Test
    void testCountScoreCorrectlyAtEnd() {
        for (int i = 0; i < 10; i++){
            game =  new Reversi();
            Game.Move[] legalMoves = game.getLegalMoves();
            while(legalMoves.length > 0) {
                game.play(legalMoves[(int)(Math.random()*legalMoves.length)]);
                legalMoves = game.getLegalMoves();
            }
            Game.Score score = game.getScore();
            IO.println(score.player1Score());
            IO.println(score.player2Score());
            char[][] grid = game.makeBoardAGrid();
            for (char[] chars : grid) {
                IO.println(Arrays.toString(chars));
            }

        }

    }

    @Test
    void testPlayerMustSkipTurnIfNoValidMoves() {
        game.play(new Game.Move(19, 'B'));
        game.play(new Game.Move(34, 'W'));
        game.play(new Game.Move(45, 'B'));
        game.play(new Game.Move(11, 'W'));
        game.play(new Game.Move(42, 'B'));
        game.play(new Game.Move(54, 'W'));
        game.play(new Game.Move(37, 'B'));
        game.play(new Game.Move(46, 'W'));
        game.play(new Game.Move(63, 'B'));
        game.play(new Game.Move(62, 'W'));
        game.play(new Game.Move(29, 'B'));
        game.play(new Game.Move(50, 'W'));
        game.play(new Game.Move(55, 'B'));
        game.play(new Game.Move(30, 'W'));
        game.play(new Game.Move(53, 'B'));
        game.play(new Game.Move(38, 'W'));
        game.play(new Game.Move(61, 'B'));
        game.play(new Game.Move(52, 'W'));
        game.play(new Game.Move(51, 'B'));
        game.play(new Game.Move(60, 'W'));
        game.play(new Game.Move(59, 'B'));
        assertEquals('B', game.getCurrentPlayer());
        game.play(ai.findBestMove(game,5));
        game.play(ai.findBestMove(game,5));
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
