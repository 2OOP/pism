package research;


import org.apache.maven.surefire.shared.io.FileDeleteStrategy;
import org.junit.jupiter.api.*;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.game.players.ArtificialPlayer;
import org.toop.game.players.ai.MCTSAI;
import org.toop.game.players.ai.mcts.MCTSAI1;
import org.toop.game.players.ai.mcts.MCTSAI2;
import org.toop.game.players.ai.mcts.MCTSAI3;
import org.toop.game.players.ai.mcts.MCTSAI4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AITest {

    private static List<Matchup> matchupList = new ArrayList<Matchup>();
    private static List<AIData> dataList = new ArrayList<AIData>();
    private static List<GameData> gameDataList = new ArrayList<GameData>();

    @BeforeAll
    public static void init() {

        var versions = new ArtificialPlayer[4];
        versions[0] = new ArtificialPlayer(new MCTSAI1(5), "MCTS V1");
        versions[1] = new ArtificialPlayer(new MCTSAI2(5), "MCTS V2");
        versions[2] = new ArtificialPlayer(new MCTSAI3(5), "MCTS V3");
        versions[3] = new ArtificialPlayer(new MCTSAI4(5), "MCTS V4");

        for (int k = 0; k < 50; k++) {
            for (int i = 0; i < versions.length; i++) {
                for (int j = i + 1; j < versions.length; j++) {
                    final int playerIndex1 = i % versions.length;
                    final int playerIndex2 = j % versions.length;
                    addMatch(versions[playerIndex1], versions[playerIndex2]);
                    addMatch(versions[playerIndex2], versions[playerIndex1]); // home vs away system
                }
            }
        }
    }

    public static void addMatch(ArtificialPlayer v1, ArtificialPlayer v2) {
        matchupList.add(new Matchup(v1, v2));
    }

    public void addAIData(AIData data) {
        dataList.add(data);
    }

    public void addGameData(GameData data) {
        gameDataList.add(data);
    }

    @Test
    public void testAIvsAI() {
        for (Matchup m : matchupList) {
            playGame(m);
        }
    }

    public void playGame(Matchup m) {
        long millisecondscounterAI1 = 0L;
        long millisecondscounterAI2 = 0L;
        List<Integer> iterationsAI1 = new ArrayList<>();
        List<Integer> iterationsAI2 = new ArrayList<>();
        final BitboardReversi match = new BitboardReversi();
        ArtificialPlayer[] players = new ArtificialPlayer[2];
        players[0] = m.getPlayer1();
        players[1] = m.getPlayer2();
        match.init(players);
        while (!match.isTerminal()) {
            final int currentAI = match.getCurrentTurn();
            final long startTime = System.nanoTime();
            final long move = players[currentAI].getMove(match);
            final long endTime = System.nanoTime();
            if (players[currentAI].getAi() instanceof MCTSAI) {
                final int lastIterations = ((MCTSAI) players[currentAI].getAi()).getLastIterations();
                if (currentAI == 0) {
                    iterationsAI1.add(lastIterations);
                    millisecondscounterAI1 += (endTime - startTime);
                } else {
                    iterationsAI2.add(lastIterations);
                    millisecondscounterAI2 += (endTime - startTime);
                }
            }
            match.play(move);
        }
        generateMatchData(m.getPlayer1().getName(), m.getPlayer2().getName(), match, iterationsAI1, iterationsAI2, millisecondscounterAI1, millisecondscounterAI2);
    }

    public void generateMatchData(
            String AI1,
            String AI2,
            BitboardReversi match,

            List<Integer> iterationsAI1,
            List<Integer> iterationsAI2,

            long millisecondscounterAI1,
            long millisecondscounterAI2
    ) {
        try {

            var ai110 = iterationsAI1.subList(0, 9);
            var ai120 = iterationsAI1.subList(10, 19);
            var ai130 = iterationsAI1.subList(20, iterationsAI1.size());

            var ai210 = iterationsAI2.subList(0, 9);
            var ai220 = iterationsAI2.subList(10, 19);
            var ai230 = iterationsAI2.subList(20, iterationsAI2.size());

            writeGamesToCSV("gameData.csv", new GameData(
                    AI1,
                    AI2,
                    getWinnerForMatch(AI1, AI2, match),
                    match.getAmountOfTurns(),

                    iterationsAI1.stream().mapToInt(Integer::intValue).sum(),
                    iterationsAI1.stream().mapToDouble(Integer::doubleValue).sum() / iterationsAI1.size(),
                    ai110.stream().mapToDouble(Integer::doubleValue).sum() / ai110.size(),
                    ai120.stream().mapToDouble(Integer::doubleValue).sum() / ai120.size(),
                    ai130.stream().mapToDouble(Integer::doubleValue).sum() / ai130.size(),

                    iterationsAI2.stream().mapToInt(Integer::intValue).sum(),
                    iterationsAI2.stream().mapToDouble(Integer::doubleValue).sum() / iterationsAI2.size(),
                    ai210.stream().mapToDouble(Integer::doubleValue).sum() / ai210.size(),
                    ai220.stream().mapToDouble(Integer::doubleValue).sum() / ai220.size(),
                    ai230.stream().mapToDouble(Integer::doubleValue).sum() / ai230.size(),

                    millisecondscounterAI1,
                    millisecondscounterAI2,
                    LocalTime.now().toString()
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
    }

    public String getWinnerForMatch(String AI1, String AI2, BitboardReversi match) {
        if (match.getWinner() == 0) {
            return AI1;
        }
        if (match.getWinner() == 1) {
            return AI2;
        } else {
            return "TIE";
        }
    }

    public void generateData(Matchup matchup, BitboardReversi match, List<Integer> iterationsAI1, List<Integer> iterationsAI2) {
        boolean matchup1Found = false;
        boolean matchup2Found = false;
        for (AIData aiData : dataList) {
            if (aiData.getAI().equals(matchup.getPlayer1().getName())) {
                matchup1Found = true;
            } if (aiData.getAI().equals(matchup.getPlayer2().getName())) {
                matchup2Found = true;
            }
        }
        if (!(matchup1Found)) {
            addAIData(new AIData(matchup.getPlayer1().getName(), 0, 0, 0, 0, 0, 0));
        }
        if (!(matchup2Found)) {
            addAIData(new AIData(matchup.getPlayer2().getName(), 0, 0, 0, 0, 0, 0));
        }

        for (AIData aiData : dataList) { // set data for player 1
            if (aiData.getAI().equals(matchup.getPlayer1().getName())) {
                aiData.setGamesPlayed(aiData.getGamesPlayed() + 1);
                aiData.setWinrate(calculateWinrate(0, aiData.getWinrate(), aiData.getGamesPlayed(), match.getWinner()));
                aiData.setAverageIterations(calculateAverageIterations(aiData.getAverageIterations(), iterationsAI1));
                aiData.setAverageIterations10(calculateAverageIterationsStartEnd(0, 10, aiData.getAverageIterations10(), iterationsAI1));
                aiData.setAverageIterations20(calculateAverageIterationsStartEnd(10, 20, aiData.getAverageIterations20(), iterationsAI1));
                aiData.setAverageIterations30(calculateAverageIterationsStartEnd(20, iterationsAI1.size(), aiData.getAverageIterations30(), iterationsAI1));
            }
        }

        for (AIData aiData : dataList) {
            if (aiData.getAI().equals(matchup.getPlayer2().getName())) {
                aiData.setGamesPlayed(aiData.getGamesPlayed() + 1);
                aiData.setWinrate(calculateWinrate(1, aiData.getWinrate(), aiData.getGamesPlayed(), match.getWinner()));
                aiData.setAverageIterations(calculateAverageIterations(aiData.getAverageIterations(), iterationsAI2));
                aiData.setAverageIterations10(calculateAverageIterationsStartEnd(0, 10, aiData.getAverageIterations10(), iterationsAI2));
                aiData.setAverageIterations20(calculateAverageIterationsStartEnd(10, 20, aiData.getAverageIterations20(), iterationsAI2));
                aiData.setAverageIterations30(calculateAverageIterationsStartEnd(20, iterationsAI2.size(), aiData.getAverageIterations30(), iterationsAI2));
            }
        }
    }

    public double calculateWinrate(int player, double winrate, long gamesPlayed, int winner) {
        double result;
        if (winner == 0 && player == 0 || winner == 1 && player == 1) {
            return (winrate * (gamesPlayed - 1) + 1) / gamesPlayed;
        } else if (winner == 0 && player == 1 || winner == 1 && player == 0) {
            return (winrate * (gamesPlayed - 1) + 0) / gamesPlayed;
        }
        return (winrate * (gamesPlayed - 1) + 0) / gamesPlayed;
    }

    public double calculateAverageIterations(double averageIterations, List<Integer> thisGameIterations) {
        double thisGameIterationsAverage = 0;
        for (int iterations = 0; iterations < thisGameIterations.size(); iterations += 1) {
            thisGameIterationsAverage += thisGameIterations.get(iterations);
        }
        thisGameIterationsAverage /= thisGameIterations.size();
        return (averageIterations + thisGameIterationsAverage) / 2;
    }

    public double calculateAverageIterationsStartEnd(int start, int end, double averageIterations, List<Integer> thisGameIterations) {
        double thisGameIterationsAverage = 0;
        for (int iterations = start; iterations < end; iterations += 1) {
            thisGameIterationsAverage += thisGameIterations.get(iterations);
        }
        thisGameIterationsAverage /= (end - start);
        return (averageIterations + thisGameIterationsAverage) / 2;
    }

    @AfterAll
    public static void writeAfterTests() {
        try {
            writeAIToCsv("Data.csv", dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeGamesToCSV(String filepath, GameData gameData) throws IOException {
        try (
                final BufferedWriter writer = Files.newBufferedWriter(
                        Paths.get(filepath),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
                final BufferedReader reader = new BufferedReader(new FileReader(filepath))
        ) {
            if (reader.readLine() == null || reader.readLine().isBlank()) {
                writer.write("Black,White,Winner,Turns Played,Black iterations,Black average iterations,Black average iterations 0-10,Black average iterations 11-20,Black average iterations 21-30,White iterations,White average iterations,White average iterations 0-10,White average iterations 11-20,White average iterations 21-30,Total Time AI1,Total Time AI2,Time");
                writer.newLine();
            }

            writer.write(
                    gameData.AI1() + "," +
                    gameData.AI2() + "," +
                    gameData.winner() + "," +
                    gameData.turns() + "," +
                    gameData.AI1totalIterations() + "," +
                    BigDecimal.valueOf(gameData.AI1averageIterations()).setScale(2, RoundingMode.DOWN) + "," +
                    BigDecimal.valueOf(gameData.AI1averageIterations10()).setScale(2, RoundingMode.DOWN) + "," +
                    BigDecimal.valueOf(gameData.AI1averageIterations20()).setScale(2, RoundingMode.DOWN) + "," +
                    BigDecimal.valueOf(gameData.AI1averageIterations30()).setScale(2, RoundingMode.DOWN) + "," +
                    gameData.AI2totalIterations() + "," +
                    BigDecimal.valueOf(gameData.AI2averageIterations()).setScale(2, RoundingMode.DOWN) + "," +
                    BigDecimal.valueOf(gameData.AI2averageIterations10()).setScale(2, RoundingMode.DOWN) + "," +
                    BigDecimal.valueOf(gameData.AI2averageIterations20()).setScale(2, RoundingMode.DOWN) + "," +
                    BigDecimal.valueOf(gameData.AI2averageIterations30()).setScale(2, RoundingMode.DOWN) + "," +
                    (gameData.millisecondsAI1() / 1_000_000L) + "," +
                    (gameData.millisecondsAI2() / 1_000_000L) + "," +
                    gameData.time());
            writer.newLine();
        }
    }

    public static void writeAIToCsv(String filepath, List<AIData> dataList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write("AI Name,Games Played,Winrate,Average Iterations,Average Iterations 0-10, Average Iterations 11-20, Average Iterations 20-30");
            writer.newLine();
            for (AIData data : dataList) {
                writer.write(
                        data.getAI() + "," +
                                data.getGamesPlayed() + "," +
                                data.getWinrate() + "," +
                                Math.round(data.getAverageIterations()) + "," +
                                Math.round(data.getAverageIterations10()) + "," +
                                Math.round(data.getAverageIterations20()) + "," +
                                Math.round(data.getAverageIterations30()));
                writer.newLine();
            }
        }
    }
}

//public class AITest {

//    private static int games = 2;
//
//    @BeforeAll
//    public static void setUp() {
//        var versions = new ArtificialPlayer[5];
//        versions[0] = new ArtificialPlayer(new RandomAI(), "Random AI");
//        versions[1] = new ArtificialPlayer(new MCTSAI1(20), "MCTS V1 AI");
//        versions[2] = new ArtificialPlayer(new org.toop.game.players.ai.mcts.MCTSAI2(20), "MCTS V2 AI");
//        versions[3] = new ArtificialPlayer(new org.toop.game.players.ai.mcts.MCTSAI3(20, 10), "MCTS V3 AI");
//        versions[4] = new ArtificialPlayer(new MCTSAI4(20, 10), "MCTS V4 AI");
//
//        for (int i = 0; i < versions.length; i++) {
//            for (int j = i + 1; j < versions.length; j++) {
//                final int playerIndex1 = i % versions.length;
//                final int playerIndex2 = j % versions.length;
//                addMatchup(versions[playerIndex1], versions[playerIndex2]);
//            }
//        }
//
//    }
//
//    @BeforeEach
//    public void setUpEach() {
//        matchupList = new ArrayList<>();
//    }
//
//    @Test
//    public void testIterationsInRealGame() {
//        for (int i = 0; i < matchups.size(); i++) {
//            testAIVSAI(games, getMatchup(i));
//        }
//    }
//
//
//    private void testAIVSAI(int games, ArtificialPlayer[] ais) {
//
//        List<List<Integer>> gamesList = new ArrayList<>();
//        for (int i = 0; i < games; i++) {
//            final BitboardReversi match = new BitboardReversi();
//            match.init(ais);
//
//            List<Integer> iterations1 = new ArrayList<>();
//            List<Integer> iterations2 = new ArrayList<>();
//
//            while (!match.isTerminal()) {
//                final int currentAI = match.getCurrentTurn();
//                final long move = ais[currentAI].getMove(match);
//                if (ais[currentAI].getAi() instanceof MCTSAI) {
//                    final int lastIterations = ((MCTSAI) ais[currentAI].getAi()).getLastIterations();
//                    if (currentAI == 0) {
//                        iterations1.add(lastIterations);
//                    } else if (currentAI == 1) {
//                        iterations2.add(lastIterations);
//                    }
//                }
//                match.play(move);
//            }
//            int winner = match.getWinner();
//            iterations1.addFirst(winner);
////            iterations1.add(-999);
//            iterations1.addAll(iterations2);
//
//            gamesList.add(iterations1);
//        }
//        matchupList.add(gamesList);
//    }
//
//    @Test
//    public void testIterationsAtFixedMove() {
//        for (ArtificialPlayer[] matchup : matchups) {
//            List<List<Integer>> gamesList = new ArrayList<>();
//            for (int j = 0; j < games; j++) {
//                final BitboardReversi match = new BitboardReversi();
//                match.init(matchup);
//
//                List<Integer> iterations = new ArrayList<>();
//
//                for (Long move : fixedMoveSet) {
//                    match.play(move);
//                    if (move == 32L) {
//                        break;
//                    }
//                }
////                iterations.add(-999);
//                var player = matchup[match.getCurrentTurn()];
//                for (int k = 0; k < 10; k++) {
//                    player.getMove(match);
//                    if (player.getAi() instanceof MCTSAI) {
//                        iterations.add(((MCTSAI) player.getAi()).getLastIterations());
//                    }
//                }
//                gamesList.add(iterations);
//            }
//            matchupList.add(gamesList);
//        }
//    }
//
//
//    @Test
//    public void testIterationsInFixedGame() {
//        for (ArtificialPlayer[] matchup : matchups) {
//            List<List<Integer>> gamesList = new ArrayList<>();
//            for (int j = 0; j < games; j++) {
//                final BitboardReversi match = new BitboardReversi();
//                match.init(matchup);
//
//                List<Integer> iterations = new ArrayList<>();
//
//                iterations.add(-999);
//
//                for (Long move : fixedMoveSet) {
//                    var player = matchup[match.getCurrentTurn()];
//                    player.getMove(match);
//                    if (player.getAi() instanceof MCTSAI) {
//                        iterations.add(((MCTSAI) player.getAi()).getLastIterations());
//                    }
//                    match.play(move);
//                }
//
//                gamesList.add(iterations);
//            }
//            matchupList.add(gamesList);
//        }
//    }
//
//    @AfterEach
//    public void tearDown() {
//        data.add(matchupList);
//    }
//
//    @AfterAll
//    public static void writeAfterTests() {
//        try {
//            writeToCsv("Data.csv", data);
//        } catch (IOException e) {
//
//        }
//    }
//
//
//    public static void writeToCsv(String filepath, List<List<List<List<Integer>>>> data) throws IOException {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
//
//            writer.write("TestID,Matchup,GameNr,Winner");
//            for (int i = 0; i < data.size(); i++) {
//                writer.write(",Iterations");
//            }
//
//            writer.newLine();
//
//            for (int TestID = 0; TestID < data.size(); TestID++) {
//                List<List<List<Integer>>> testCase = data.get(TestID);
//
//                for (int matchupNr = 0; matchupNr < testCase.size(); matchupNr++) {
//                    List<List<Integer>> matchup = testCase.get(matchupNr);
//
//                    for (int gameNr = 0; gameNr < matchup.size(); gameNr++) {
//                        List<Integer> game = matchup.get(gameNr);
//                        writer.write((TestID + 1) + "," + (getMatchupName(matchupNr)) + "," + (gameNr + 1));
//                        for (int i = 0; i < game.size(); i++) {
//                            if (i == 0) {
//                                writer.write("," + getWinnerFromMatchup(game.get(i), matchupNr));
//                            } else {
//                                writer.write("," + game.get(i));
//                            }
//                        }
//                        writer.newLine();
//                    }
//                }
//            }
//        }
//
//    }
//
//
//    private static final List<List<List<List<Integer>>>> data = new ArrayList<>();
//    private List<List<List<Integer>>> matchupList = new ArrayList<>();
//    private static final List<String> matchupNames = new ArrayList<>();
//    private static final List<ArtificialPlayer[]> matchups = new ArrayList<>();
//
//    private static String getMatchupName(int matchupNr) {
//        return matchupNames.get(matchupNr);
//    }
//
//    private static ArtificialPlayer[] getMatchup(int matchupNr) {
//        return matchups.get(matchupNr);
//    }
//
//    private static String getWinnerFromMatchup(Integer winner, int matchupNr) {
//        String matchup = matchupNames.get(matchupNr);
//
//        String[] parts = matchup.split(" vs ");
//
//        if (parts.length != 2) {
//            return "Invalid matchup formatting.";
//        }
//
//        return winner == 0 ? parts[0] : winner == 1 ? parts[1] : winner == -999 ? "NVT" : "Tie";
//    }
//
//    private static void addMatchup(ArtificialPlayer player1, ArtificialPlayer player2) {
//        matchups.add(new ArtificialPlayer[]{player1, player2});
//        matchupNames.add(player1.getName() + " vs " + player2.getName());
//    }
//}

//    private final Long[] fixedMoveSet = new Long[]{17592186044416L,
//            35184372088832L,
//            67108864L,
//            8796093022208L,
//            2251799813685248L,
//            288230376151711744L,
//            70368744177664L,
//            1125899906842624L,
//            137438953472L,
//            140737488355328L,
//            4503599627370496L,
//            2305843009213693952L,
//            18014398509481984L,
//            274877906944L,
//            576460752303423488L,
//            -9223372036854775808L,
//            549755813888L,
//            1152921504606846976L,
//            144115188075855872L,
//            72057594037927936L,
//            36028797018963968L,
//            17179869184L,
//            2199023255552L,
//            1048576L,
//            4398046511104L,
//            281474976710656L,
//            9007199254740992L,
//            2147483648L,
//            1073741824L,
//            33554432L,
//            262144L,
//            8388608L,
//            8192L,
//            4611686018427387904L,
//            4294967296L,
//            524288L,
//            4096L,
//            16777216L,
//            65536L,
//            32L,
//            2048L,
//            8L,
//            4L,
//            8589934592L,
//            16L,
//            2097152L,
//            4194304L,
//            1024L,
//            512L,
//            16384L,
//            536870912L,
//            1099511627776L,
//            64L,
//            562949953421312L,
//            128L,
//            1L,
//            32768L,
//            2L,
//            256L,
//            131072L};
// }

