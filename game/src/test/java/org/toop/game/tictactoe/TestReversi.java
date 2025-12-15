package org.toop.game.tictactoe;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.games.reversi.BitboardReversi;
import org.toop.game.players.ArtificialPlayer;
import org.toop.game.players.ai.MiniMaxAI;
import org.toop.game.players.ai.RandomAI;

import static org.junit.jupiter.api.Assertions.*;

public class TestReversi {
    private BitboardReversi game;
    private Player[] players;

    @BeforeEach
    void setup(){
        players = new Player[2];
        players[0] = new ArtificialPlayer<BitboardReversi>(new RandomAI<BitboardReversi>(),"randomAI");
        players[1] = new ArtificialPlayer<BitboardReversi>(new MiniMaxAI<BitboardReversi>(10),"miniMaxAI");
        game = new BitboardReversi(players);

    }

    @Test
    void testCorrectStartPiecesPlaced() {
        assertNotNull(game);
        long[] board = game.getBoard();
        IO.println(Long.toBinaryString(board[0]));
        IO.println(Long.toBinaryString(board[1]));
        long black = board[0];
        long white = board[1];
        assertEquals(1L, ((white >>> 27) & 1L)); //checks if the 27-shifted long has a 1 bit
        assertEquals(1L, ((black >>> 28) & 1L));
        assertEquals(1L, ((black >>> 35) & 1L));
        assertEquals(1L, ((white >>> 36) & 1L));
    }

    @Test
    void testPlayGames(){
        int totalGames = 1;
        long start = System.nanoTime();
        long midtime = System.nanoTime();
        int p1wins = 0;
        int p2wins = 0;
        int draws = 0;

        for (int i = 0; i < totalGames; i++){
            game = new BitboardReversi(players);
            while(!game.isGameOver()){
                midtime = System.nanoTime();
                int currentTurn = game.getCurrentTurn();
                long move = players[currentTurn].getMove(game.deepCopy());
                game.play(move);
                IO.println(System.nanoTime() - midtime);
            }
            switch (game.getWinner()){
                case 0:
                    p1wins++;
                    break;
                case 1:
                    p2wins++;
                    break;
                case -1:
                    draws++;
                    break;

            }
        }
        System.out.println(System.nanoTime() - start);
        IO.println(p1wins + " " + p2wins + " " + draws);
        assertEquals(totalGames, p1wins + p2wins + draws);
        IO.println("p1 wr: " + p1wins + "/" + totalGames + " = " + (double) p1wins / totalGames);
    }
}
