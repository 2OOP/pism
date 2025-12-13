//package org.toop.framework.networking.server;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.toop.framework.gameFramework.model.game.PlayResult;
//import org.toop.framework.gameFramework.model.game.TurnBasedGame;
//import org.toop.framework.gameFramework.model.player.Player;
//
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class ServerTest {
//
//    static class TurnBasedGameMock implements TurnBasedGame {
//        private Player[] players;
//
//        public TurnBasedGameMock() {}
//
//        @Override
//        public void init(Player[] players) {
//            this.players = players;
//        }
//
//        @Override
//        public int getCurrentTurn() {
//            return 0;
//        }
//
//        @Override
//        public int getPlayerCount() {
//            return 0;
//        }
//
//        @Override
//        public int getWinner() {
//            return 0;
//        }
//
//        @Override
//        public long[] getBoard() {
//            return new long[0];
//        }
//
//        @Override
//        public TurnBasedGame deepCopy() {
//            return null;
//        }
//
//        @Override
//        public long getLegalMoves() {
//            return 0;
//        }
//
//        @Override
//        public PlayResult play(long move) {
//            return null;
//        }
//
//        @Override
//        public Player getPlayer(int index) {
//            return null;
//        }
//
//    }
//
//    static class TestUser implements ServerUser {
//
//        final private long id;
//
//        private String name;
//
//        public TestUser(long id, String name) {
//            this.id = id;
//            this.name = name;
//        }
//
//        @Override
//        public long id() {
//            return id;
//        }
//
//        @Override
//        public String name() {
//            return name;
//        }
//
//        @Override
//        public Game[] games() {
//            return new Game[0];
//        }
//
//        @Override
//        public void addGame(Game game) {
//
//        }
//
//        @Override
//        public void removeGame(Game game) {
//
//        }
//
//        @Override
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public void sendMessage(String message) {
//
//        }
//    }
//
//    private Server server;
//    private Duration waitTime = Duration.ofSeconds(2);
//
//    @BeforeEach
//    void setup() {
//
//        var games = new ConcurrentHashMap<String, Class<? extends TurnBasedGame>>();
//        games.put("tictactoe", TurnBasedGameMock.class);
//        games.put("reversi", TurnBasedGameMock.class);
//
//        server = new Server(games, waitTime);
//    }
//
//    @Test
//    void testGameTypes() {
//        String[] expected = {"tictactoe", "reversi"};
//        String[] actual = server.gameTypes();
//
//        Arrays.sort(expected);
//        Arrays.sort(actual);
//
//        Assertions.assertArrayEquals(expected, actual);
//    }
//
//    @Test
//    void testChallenge() {
//        server.addUser(new TestUser(1, "test1"));
//        server.addUser(new TestUser(2, "test2"));
//        server.challengeUser("test1", "test2", "tictactoe");
//
//        IO.println(server.gameChallenges());
//
//        Assertions.assertEquals(1, server.gameChallenges().size());
//
//        try {
//            Thread.sleep(waitTime.plusMillis(100));
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        Assertions.assertEquals(0, server.gameChallenges().size());
//    }
//
//    @Test
//    void testStartGame() {
//        server.startGame("tictactoe", new User(0, "A"), new User(1, "B"));
//        Assertions.assertEquals(1, server.ongoingGames().size());
//        server.startGame("reversi", new User(0, "A"), new User(1, "B"));
//        Assertions.assertEquals(2, server.ongoingGames().size());
//    }
//
//
//}
