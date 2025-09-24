package org.toop.game.tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.games.GameBase;
import org.toop.games.Player;

class GameBaseTest {

    private static class TestGame extends GameBase {
        public TestGame(int size, Player p1, Player p2) {
            super(size, p1, p2);
        }

        @Override
        public State play(int index) {
            if (!isInside(index)) return State.INVALID;
            grid[index] = getCurrentPlayer().getSymbol();
            // Just alternate players for testing
            currentPlayer = (currentPlayer + 1) % 2;
            return State.NORMAL;
        }
    }

    private GameBase game;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        player1 = new Player("A", 'X');
        player2 = new Player("B", 'O');
        game = new TestGame(3, player1, player2);
    }

    @Test
    void testConstructor_initializesGridAndPlayers() {
        assertEquals(3, game.getSize());
        assertEquals(9, game.getGrid().length);

        for (char c : game.getGrid()) {
            assertEquals(GameBase.EMPTY, c);
        }

        assertEquals(player1, game.getPlayers()[0]);
        assertEquals(player2, game.getPlayers()[1]);
        assertEquals(player1, game.getCurrentPlayer());
    }

    @Test
    void testIsInside_returnsTrueForValidIndices() {
        for (int i = 0; i < 9; i++) {
            assertTrue(game.isInside(i));
        }
    }

    @Test
    void testIsInside_returnsFalseForInvalidIndices() {
        assertFalse(game.isInside(-1));
        assertFalse(game.isInside(9));
        assertFalse(game.isInside(100));
    }

    @Test
    void testPlay_alternatesPlayersAndMarksGrid() {
        // First move
        assertEquals(GameBase.State.NORMAL, game.play(0));
        assertEquals('X', game.getGrid()[0]);
        assertEquals(player2, game.getCurrentPlayer());

        // Second move
        assertEquals(GameBase.State.NORMAL, game.play(1));
        assertEquals('O', game.getGrid()[1]);
        assertEquals(player1, game.getCurrentPlayer());
    }

    @Test
    void testPlay_invalidIndexReturnsInvalid() {
        assertEquals(GameBase.State.INVALID, game.play(-1));
        assertEquals(GameBase.State.INVALID, game.play(9));
    }
}
