
import org.junit.jupiter.api.Test;
import org.toop.game.MinMaxTTT;
import org.toop.game.TTT;

import static org.junit.jupiter.api.Assertions.*;

class MinMaxTTTTest {

    // makegame makes a board situation so we can test the ai. thats it really, the rest is easy to follow id say
    private TTT makeGame(String board, int currentPlayer) {
        TTT game = new TTT("AI", "Human");
        // Fill the board
        for (int i = 0; i < board.length(); i++) {
            char c = board.charAt(i);
            game.grid[i] = c;
            if (c != ' ') game.moveCount++;
        }
        game.currentPlayer = currentPlayer;
        return game;
    }

    @Test
    void testFindBestMove_AIImmediateWin() {
        TTT game = makeGame("XX OO    ", 0);
        MinMaxTTT ai = new MinMaxTTT();
        int bestMove = ai.findBestMove(game);
        assertEquals(2, bestMove, "AI has to take winning move at 2");
    }

    @Test
    void testFindBestMove_BlockOpponentWin() {
        TTT game = makeGame("OO X    ", 0); // 0 = AI's turn
        MinMaxTTT ai = new MinMaxTTT();
        int bestMove = ai.findBestMove(game);
        assertEquals(2, bestMove, "AI should block opponent win at 2");
    }

    @Test
    void testFindBestMove_ChooseDrawIfNoWin() {
        TTT game = makeGame("XOXOX O  ", 0);
        MinMaxTTT ai = new MinMaxTTT();
        int bestMove = ai.findBestMove(game);
        assertTrue(bestMove == 6 || bestMove == 8, "AI should draw");
    }

    @Test
    void testMinimax_ScoreWin() {
        TTT game = makeGame("XXX      ", 0);
        MinMaxTTT ai = new MinMaxTTT();
        int score = ai.doMinimax(game, 5, false);
        assertTrue(score > 0, "AI win scored positively");
    }

    @Test
    void testMinimax_ScoreLoss() {
        TTT game = makeGame("OOO      ", 1);
        MinMaxTTT ai = new MinMaxTTT();
        int score = ai.doMinimax(game, 5, true);
        assertTrue(score < 0, "AI loss is negative");
    }

    @Test
    void testMinimax_ScoreDraw() {
        TTT game = makeGame("XOXOXOOXO", 0);
        MinMaxTTT ai = new MinMaxTTT();
        int score = ai.doMinimax(game, 5, true);
        assertEquals(0, score, "Draw should be zero!");
    }

    @Test
    void testMiniMax_MultipleMoves() {
        TTT game = makeGame(" X OX  O ", 0);
        MinMaxTTT ai = new MinMaxTTT();
        int bestMove = ai.findBestMove(game);
        assertTrue(bestMove == 0 || bestMove == 2, "Can look at multiple moves!");
    }
}