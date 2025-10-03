package org.toop.game.tictactoe;

import org.toop.game.Game;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
		game.play(new Game.Move(0, 'X'));
		game.play(new Game.Move(3, 'O'));
		game.play(new Game.Move(1, 'X'));
		game.play(new Game.Move(4, 'O'));

		final Game.Move move = ai.findBestMove(game, 1);

		assertNotNull(move);
		assertEquals('X', move.value());
		assertEquals(2, move.position());
	}

	@Test
	void testBestMove_blockOpponentWinDepth1() {
		// - - -
		// O - -
		// X X -
		game.play(new Game.Move(6, 'X'));
		game.play(new Game.Move(3, 'O'));
		game.play(new Game.Move(7, 'X'));

		final Game.Move move = ai.findBestMove(game, 1);

		assertNotNull(move);
		assertEquals('O', move.value());
		assertEquals(8, move.position());
	}

	@Test
	void testBestMove_preferCornerOnEmpty() {
		final Game.Move move = ai.findBestMove(game, 0);

		assertNotNull(move);
		assertEquals('X', move.value());
		assertTrue(Set.of(0, 2, 6, 8).contains(move.position()));
	}

	@Test
	void testBestMove_findBestMoveDraw() {
		// O X -
		// - O X
 		// X O X
		game.play(new Game.Move(1, 'X'));
		game.play(new Game.Move(0, 'O'));
		game.play(new Game.Move(5, 'X'));
		game.play(new Game.Move(4, 'O'));
		game.play(new Game.Move(6, 'X'));
		game.play(new Game.Move(7, 'O'));
		game.play(new Game.Move(8, 'X'));

		final Game.Move move = ai.findBestMove(game, game.getLegalMoves().length);

		assertNotNull(move);
		assertEquals('O', move.value());
		assertEquals(2, move.position());
	}
}