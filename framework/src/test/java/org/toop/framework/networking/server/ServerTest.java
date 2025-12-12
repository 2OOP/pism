package org.toop.framework.networking.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {

    static class TurnBasedGameMock implements TurnBasedGame {
        private Player[] players;

        public TurnBasedGameMock() {}

        @Override
        public void init(Player[] players) {
            this.players = players;
        }

        @Override
        public int getCurrentTurn() {
            return 0;
        }

        @Override
        public int getPlayerCount() {
            return 0;
        }

        @Override
        public int getWinner() {
            return 0;
        }

        @Override
        public long[] getBoard() {
            return new long[0];
        }

        @Override
        public TurnBasedGame deepCopy() {
            return null;
        }

        @Override
        public long getLegalMoves() {
            return 0;
        }

        @Override
        public PlayResult play(long move) {
            return null;
        }

        @Override
        public Player getPlayer(int index) {
            return null;
        }

    }

    private Server server;

    @BeforeEach
    void setup() {

        var games = new ConcurrentHashMap<String, Class<? extends TurnBasedGame>>();
        games.put("tictactoe", TurnBasedGameMock.class);
        games.put("reversi", TurnBasedGameMock.class);

        server = new Server(games);
    }

    @Test
    void testGameTypes() {
        String[] expected = {"tictactoe", "reversi"};
        String[] actual = server.gameTypes();

        Arrays.sort(expected);
        Arrays.sort(actual);

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    void testStartGame() {
        server.startGame("tictactoe", new User(0, "A"), new User(1, "B"));
        Assertions.assertEquals(1, server.ongoingGames().size());
    }
}
