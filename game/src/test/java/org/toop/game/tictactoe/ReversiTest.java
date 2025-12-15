/*//todo fix this mess



package org.toop.game.tictactoe;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.framework.gameFramework.model.player.AbstractAI;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.AI;
import org.toop.game.enumerators.GameState;
import org.toop.game.games.reversi.ReversiAIR;
import org.toop.game.games.reversi.ReversiR;
import org.toop.game.records.Move;
import org.toop.game.reversi.Reversi;
import org.toop.game.reversi.ReversiAI;
import org.toop.game.players.ai.ReversiAIML;
import org.toop.game.games.reversi.ReversiAISimple;

import static org.junit.jupiter.api.Assertions.*;

class ReversiTest {
    private ReversiR game;
    private ReversiAIR ai;
    private ReversiAIML aiml;
    private ReversiAISimple aiSimple;
    private AbstractAI<ReversiR> player1;
    private AbstractAI<ReversiR> player2;
    private Player[] players = new Player[2];

    @BeforeEach
    void setup() {
        game = new ReversiR(players);
        ai = new ReversiAIR();
        aiml = new ReversiAIML();
        aiSimple = new ReversiAISimple();

    }


    @Test
    void testCorrectStartPiecesPlaced() {
        assertNotNull(game);
        assertEquals('W', game.getBoard()[27]);
        assertEquals('B', game.getBoard()[28]);
        assertEquals('B', game.getBoard()[35]);
        assertEquals('W', game.getBoard()[36]);
    }

    @Test
    void testGetLegalMovesAtStart() {
        Move[] moves = game.getLegalMoves();
        List<Move> expectedMoves = List.of(
                new Move(19, 'B'),
                new Move(26, 'B'),
                new Move(37, 'B'),
                new Move(44, 'B')
        );
        assertNotNull(moves);
        assertTrue(moves.length > 0);
        assertMovesMatchIgnoreOrder(expectedMoves, Arrays.asList(moves));
    }

    private void assertMovesMatchIgnoreOrder(List<Move> expected, List<Move> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertTrue(actual.contains(expected.get(i)));
            assertTrue(expected.contains(actual.get(i)));
        }
    }

    @Test
    void testMakeValidMoveFlipsPieces() {
        game.play(new Move(19, 'B'));
        assertEquals('B', game.getBoard()[19]);
        assertEquals('B', game.getBoard()[27], "Piece should have flipped to B");
    }

    @Test
    void testMakeInvalidMoveDoesNothing() {
        char[] before = game.getBoard().clone();
        game.play(new Move(0, 'B'));
        assertArrayEquals(before, game.getBoard(), "Board should not change on invalid move");
    }

    @Test
    void testTurnSwitchesAfterValidMove() {
        char current = game.getCurrentPlayer();
        game.play(game.getLegalMoves()[0]);
        assertNotEquals(current, game.getCurrentPlayer(), "Player turn should switch after a valid move");
    }

    @Test
    void testCountScoreCorrectlyAtStart() {
        long start = System.nanoTime();
        Reversi.Score score = game.getScore();
        assertEquals(2, score.player1Score()); // Black
        assertEquals(2, score.player2Score()); // White
        long end = System.nanoTime();
        IO.println((end - start));
    }

    @Test
    void zLegalMovesInCertainPosition() {
        game.play(new Move(19, 'B'));
        game.play(new Move(20, 'W'));
        Move[] moves = game.getLegalMoves();
        List<Move> expectedMoves = List.of(
                new Move(13, 'B'),
                new Move(21, 'B'),
                new Move(29, 'B'),
                new Move(37, 'B'),
                new Move(45, 'B'));
        assertNotNull(moves);
        assertTrue(moves.length > 0);
        IO.println(Arrays.toString(moves));
        assertMovesMatchIgnoreOrder(expectedMoves, Arrays.asList(moves));
    }

    @Test
    void testCountScoreCorrectlyAtEnd() {
        for (int i = 0; i < 1; i++) {
            game = new Reversi();
            Move[] legalMoves = game.getLegalMoves();
            while (legalMoves.length > 0) {
                game.play(legalMoves[(int) (Math.random() * legalMoves.length)]);
                legalMoves = game.getLegalMoves();
            }
            Reversi.Score score = game.getScore();
            IO.println(score.player1Score());
            IO.println(score.player2Score());

            for (int r = 0; r < game.getRowSize(); r++) {
                char[] row = Arrays.copyOfRange(game.getBoard(), r * game.getColumnSize(), (r + 1) * game.getColumnSize());
                IO.println(Arrays.toString(row));
            }
        }
    }

    @Test
    void testPlayerMustSkipTurnIfNoValidMoves() {
        game.play(new Move(19, 'B'));
        game.play(new Move(34, 'W'));
        game.play(new Move(45, 'B'));
        game.play(new Move(11, 'W'));
        game.play(new Move(42, 'B'));
        game.play(new Move(54, 'W'));
        game.play(new Move(37, 'B'));
        game.play(new Move(46, 'W'));
        game.play(new Move(63, 'B'));
        game.play(new Move(62, 'W'));
        game.play(new Move(29, 'B'));
        game.play(new Move(50, 'W'));
        game.play(new Move(55, 'B'));
        game.play(new Move(30, 'W'));
        game.play(new Move(53, 'B'));
        game.play(new Move(38, 'W'));
        game.play(new Move(61, 'B'));
        game.play(new Move(52, 'W'));
        game.play(new Move(51, 'B'));
        game.play(new Move(60, 'W'));
        game.play(new Move(59, 'B'));
        assertEquals('B', game.getCurrentPlayer());
        game.play(ai.findBestMove(game, 5));
        game.play(ai.findBestMove(game, 5));
    }

    @Test
    void testGameShouldEndIfNoValidMoves() {
        //European Grand Prix Ghent 2017: Replay Hassan - Verstuyft J. (3-17)
        game.play(new Move(19, 'B'));
        game.play(new Move(20, 'W'));
        game.play(new Move(29, 'B'));
        game.play(new Move(22, 'W'));
        game.play(new Move(21, 'B'));
        game.play(new Move(34, 'W'));
        game.play(new Move(23, 'B'));
        game.play(new Move(13, 'W'));
        game.play(new Move(26, 'B'));
        game.play(new Move(18, 'W'));
        game.play(new Move(12, 'B'));
        game.play(new Move(4, 'W'));
        game.play(new Move(17, 'B'));
        game.play(new Move(31, 'W'));
        GameState stateTurn15 = game.play(new Move(39, 'B'));
        assertEquals(GameState.NORMAL, stateTurn15);
        GameState stateTurn16 = game.play(new Move(16, 'W'));
        assertEquals(GameState.WIN, stateTurn16);
        GameState stateTurn17 = game.play(new Move(5, 'B'));
        assertNull(stateTurn17);
        Reversi.Score score = game.getScore();
        assertEquals(3, score.player1Score());
        assertEquals(17, score.player2Score());
    }

    @Test
    void testAISelectsLegalMove() {
        Move move = ai.findBestMove(game, 4);
        assertNotNull(move);
        assertTrue(containsMove(game.getLegalMoves(), move), "AI should always choose a legal move");
    }

    private boolean containsMove(Move[] moves, Move move) {
        for (Move m : moves) {
            if (m.equals(move)) return true;
        }
        return false;
    }

    @Test
    void testAis() {
        player1 = aiml;
        player2 = ai;
        testAIvsAIML();
        player2 = aiSimple;
        testAIvsAIML();
        player1 = ai;
        testAIvsAIML();
        player2 = aiml;
        testAIvsAIML();
        player1 = aiml;
        testAIvsAIML();
        player1 = aiSimple;
        testAIvsAIML();
    }

    @Test
    void testAIvsAIML() {
        if(player1 == null || player2 == null) {
            player1 = aiml;
            player2 = ai;
        }
        int totalGames = 2000;
        IO.println("Testing... " + player1.getClass().getSimpleName() + " vs " + player2.getClass().getSimpleName() + " for " + totalGames + " games");
        int p1wins = 0;
        int p2wins = 0;
        int draws = 0;
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < totalGames; i++) {
            game = new ReversiR();
            while (!game.isGameOver()) {
                char curr = game.getCurrentPlayer();
                Move move;
                if (curr == 'B') {
                    move = player1.findBestMove(game, 5);
                } else {
                    move = player2.findBestMove(game, 5);
                }
                if (i%500 == 0) moves.add(move.position());
                game.play(move);
            }
            if (i%500 == 0) {
                IO.println(moves);
                moves.clear();
            }
            int winner = game.getWinner();
            if (winner == 1) {
                p1wins++;
            } else if (winner == 2) {
                p2wins++;
            } else {
                draws++;
            }
        }
        IO.println("p1 winrate: " + p1wins + "/" + totalGames + " = " + (double) p1wins / totalGames + "\np2wins: " + p2wins + " draws: " + draws);
    }
}

 */