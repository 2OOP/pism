package org.toop.game.tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.games.Player;

class PlayerTest {

    private Player playerA;
    private Player playerB;

    @BeforeEach
    void setup() {
        playerA = new Player("testA", 'X');
        playerB = new Player("testB", 'O');
    }

    @Test
    void testNameGetter_returnsTrueForValidName() {
        assertEquals("testA", playerA.getName());
        assertEquals("testB", playerB.getName());
    }

    @Test
    void testSymbolGetter_returnsTrueForValidSymbol() {
        assertEquals('X', playerA.getSymbol());
        assertEquals('O', playerB.getSymbol());
    }
}
