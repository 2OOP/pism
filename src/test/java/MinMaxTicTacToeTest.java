
import org.junit.jupiter.api.Test;
import org.toop.game.tictactoe.*;

import static org.junit.jupiter.api.Assertions.*;

class MinMaxTicTacToeTest {

    // makegame makes a board situation so we can test the AI. that's it really, the rest is easy to follow id say
    private TicTacToe makeGame(String board, int currentPlayer) {
        TicTacToe game = new TicTacToe("AI", "Human");
        // Fill the board
        for (int i = 0; i < board.length(); i++) {
            char c = board.charAt(i);
            game.grid[i] = c;
            if (c != '-') game.movesLeft--;
        }
        game.currentPlayer = currentPlayer;
        return game;
    }

    @Test
    void testFindBestMove_AIImmediateWin() {
        TicTacToe game = makeGame("XX-OO----", 0);
        MinMaxTicTacToe ai = new MinMaxTicTacToe();
        int bestMove = ai.findBestMove(game);
        assertEquals(2, bestMove, "AI has to take winning move at 2");
    }

    @Test
    void testFindBestMove_BlockOpponentWin() {
        TicTacToe game = makeGame("OO-X----", 0); // 0 = AI's turn
        MinMaxTicTacToe ai = new MinMaxTicTacToe();
        int bestMove = ai.findBestMove(game);
        assertEquals(2, bestMove, "AI should block opponent win at 2");
    }

    @Test
    void testFindBestMove_ChooseDrawIfNoWin() {
        TicTacToe game = makeGame("XOXOX-O--", 0);
        MinMaxTicTacToe ai = new MinMaxTicTacToe();
        int bestMove = ai.findBestMove(game);
        assertTrue(bestMove == 6 || bestMove == 8, "AI should draw");
    }

    @Test
    void testMinimax_ScoreWin() {
        TicTacToe game = makeGame("XXX------", 0);
        MinMaxTicTacToe ai = new MinMaxTicTacToe();
        int score = ai.doMinimax(game, 5, false);
        assertTrue(score > 0, "AI win scored positively");
    }

    @Test
    void testMinimax_ScoreLoss() {
        TicTacToe game = makeGame("OOO      ", 1);
        MinMaxTicTacToe ai = new MinMaxTicTacToe();
        int score = ai.doMinimax(game, 5, true);
        assertTrue(score < 0, "AI loss is negative");
    }

    @Test
    void testMinimax_ScoreDraw() {
        TicTacToe game = makeGame("XOXOXOOXO", 0);
        MinMaxTicTacToe ai = new MinMaxTicTacToe();
        int score = ai.doMinimax(game, 5, true);
        assertEquals(0, score, "Draw should be zero!");
    }

    @Test
    void testMiniMax_MultipleMoves() {
        TicTacToe game = makeGame(" X-OX--O-", 0);
        MinMaxTicTacToe ai = new MinMaxTicTacToe();
        int bestMove = ai.findBestMove(game);
        assertTrue(bestMove == 0 || bestMove == 2, "Can look at multiple moves!");
    }
}
