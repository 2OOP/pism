package research;


import org.junit.jupiter.api.*;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.game.players.ArtificialPlayer;
import org.toop.game.players.ai.MCTSAI;
import org.toop.game.players.ai.RandomAI;
import org.toop.game.players.ai.mcts.MCTSAI1;
import org.toop.game.players.ai.mcts.MCTSAI4;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AITest {

    private static int games = 2;

    @BeforeAll
    public static void setUp() {
        var versions = new ArtificialPlayer[5];
        versions[0] = new ArtificialPlayer(new RandomAI(), "Random AI");
        versions[1] = new ArtificialPlayer(new MCTSAI1(20), "MCTS V1 AI");
        versions[2] = new ArtificialPlayer(new org.toop.game.players.ai.mcts.MCTSAI2(20), "MCTS V2 AI");
        versions[3] = new ArtificialPlayer(new org.toop.game.players.ai.mcts.MCTSAI3(20, 10), "MCTS V3 AI");
        versions[4] = new ArtificialPlayer(new MCTSAI4(20, 10), "MCTS V4 AI");

        for (int i = 0; i < versions.length; i++) {
            for (int j = i + 1; j < versions.length; j++) {
                final int playerIndex1 = i % versions.length;
                final int playerIndex2 = j % versions.length;
                addMatchup(versions[playerIndex1], versions[playerIndex2]);
            }
        }

    }

    @BeforeEach
    public void setUpEach() {
        matchupList = new ArrayList<>();
    }

    @Test
    public void testIterationsInRealGame() {
        for (int i = 0; i < matchups.size(); i++) {
            testAIVSAI(games, getMatchup(i));
        }
    }


    private void testAIVSAI(int games, ArtificialPlayer[] ais) {

        List<List<Integer>> gamesList = new ArrayList<>();
        for (int i = 0; i < games; i++) {
            final BitboardReversi match = new BitboardReversi();
            match.init(ais);

            List<Integer> iterations1 = new ArrayList<>();
            List<Integer> iterations2 = new ArrayList<>();

            while (!match.isTerminal()) {
                final int currentAI = match.getCurrentTurn();
                final long move = ais[currentAI].getMove(match);
                if (ais[currentAI].getAi() instanceof MCTSAI) {
                    final int lastIterations = ((MCTSAI) ais[currentAI].getAi()).getLastIterations();
                    if (currentAI == 0) {
                        iterations1.add(lastIterations);
                    }
                    else if (currentAI == 1){
                        iterations2.add(lastIterations);
                    }
                }
                match.play(move);
            }
            int winner = match.getWinner();
            iterations1.addFirst(winner);
            iterations1.add(-999);      //separator
            iterations1.addAll(iterations2);

            gamesList.add(iterations1);
        }
        matchupList.add(gamesList);
    }

    @Test
    public void testIterationsAtFixedMove()
    {
        for (ArtificialPlayer[] matchup : matchups) {
            List<List<Integer>> gamesList = new ArrayList<>();
            for (int j = 0; j < games; j++) {
                final BitboardReversi match = new BitboardReversi();
                match.init(matchup);

                List<Integer> iterations = new ArrayList<>();

                for (Long move : fixedMoveSet) {
                    match.play(move);
                    if (move == 32L) {
                        break;
                    }
                }
                iterations.add(-999);
                var player = matchup[match.getCurrentTurn()];
                for (int k = 0; k < 10; k++) {
                    player.getMove(match);
                    if (player.getAi() instanceof MCTSAI) {
                        iterations.add(((MCTSAI) player.getAi()).getLastIterations());
                    }
                }
                gamesList.add(iterations);
            }
            matchupList.add(gamesList);
        }
    }


    @Test
    public void testIterationsInFixedGame(){
        for (ArtificialPlayer[] matchup : matchups) {
            List<List<Integer>> gamesList = new ArrayList<>();
            for (int j = 0; j < games; j++) {
                final BitboardReversi match = new BitboardReversi();
                match.init(matchup);

                List<Integer> iterations = new ArrayList<>();

                iterations.add(-999);

                for (Long move : fixedMoveSet) {
                    var player = matchup[match.getCurrentTurn()];
                    player.getMove(match);
                    if (player.getAi() instanceof MCTSAI) {
                        iterations.add(((MCTSAI) player.getAi()).getLastIterations());
                    }
                    match.play(move);
                }

                gamesList.add(iterations);
            }
            matchupList.add(gamesList);
        }
    }

    @AfterEach
    public void tearDown() {
        data.add(matchupList);
    }

    @AfterAll
    public static void writeAfterTests(){
        try {
            writeToCsv("Data.csv", data);
        }catch (IOException e) {

        }
    }


    public static void writeToCsv(String filepath, List<List<List<List<Integer>>>> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {

            writer.write("TestID,Matchup,GameNr,Winner");
            for (int i = 0; i < data.size(); i++) {
                writer.write(",Iterations");
            }

            writer.newLine();

            for (int TestID = 0; TestID < data.size(); TestID++) {
                List<List<List<Integer>>> testCase = data.get(TestID);

                for (int matchupNr = 0; matchupNr < testCase.size(); matchupNr++) {
                    List<List<Integer>> matchup = testCase.get(matchupNr);

                    for (int gameNr = 0; gameNr < matchup.size(); gameNr++) {
                        List<Integer> game = matchup.get(gameNr);
                        writer.write((TestID + 1) + "," + (getMatchupName(matchupNr)) + "," + (gameNr + 1));
                        for (int i = 0; i < game.size(); i++) {
                            if (i == 0) {
                                writer.write("," + getWinnerFromMatchup(game.get(i),matchupNr));
                            } else {
                                writer.write("," + game.get(i));
                            }
                        }
                        writer.newLine();
                    }
                }
            }
        }

    }


    private static final List<List<List<List<Integer>>>> data = new ArrayList<>();
    private List<List<List<Integer>>> matchupList = new ArrayList<>();
    private static final List<String> matchupNames = new ArrayList<>();
    private static final List<ArtificialPlayer[]> matchups = new ArrayList<>();
    private static String getMatchupName(int matchupNr){
        return matchupNames.get(matchupNr);
    }
    private static ArtificialPlayer[] getMatchup(int matchupNr){
        return matchups.get(matchupNr);
    }
    private static String getWinnerFromMatchup(Integer winner, int matchupNr){
        String matchup = matchupNames.get(matchupNr);

        String[] parts = matchup.split(" vs ");

        if (parts.length != 2) {
            return "Invalid matchup formatting.";
        }

        return winner == 0 ? parts[0] : winner == 1? parts[1] : winner == -999? "NVT" : "Tie";
    }

    private static void addMatchup(ArtificialPlayer player1, ArtificialPlayer player2){
        matchups.add(new ArtificialPlayer[] {player1,player2});
        matchupNames.add(player1.getName() + " vs " + player2.getName());
    }

    private final Long[] fixedMoveSet = new Long[]{17592186044416L,
            35184372088832L,
            67108864L,
            8796093022208L,
            2251799813685248L,
            288230376151711744L,
            70368744177664L,
            1125899906842624L,
            137438953472L,
            140737488355328L,
            4503599627370496L,
            2305843009213693952L,
            18014398509481984L,
            274877906944L,
            576460752303423488L,
            -9223372036854775808L,
            549755813888L,
            1152921504606846976L,
            144115188075855872L,
            72057594037927936L,
            36028797018963968L,
            17179869184L,
            2199023255552L,
            1048576L,
            4398046511104L,
            281474976710656L,
            9007199254740992L,
            2147483648L,
            1073741824L,
            33554432L,
            262144L,
            8388608L,
            8192L,
            4611686018427387904L,
            4294967296L,
            524288L,
            4096L,
            16777216L,
            65536L,
            32L,
            2048L,
            8L,
            4L,
            8589934592L,
            16L,
            2097152L,
            4194304L,
            1024L,
            512L,
            16384L,
            536870912L,
            1099511627776L,
            64L,
            562949953421312L,
            128L,
            1L,
            32768L,
            2L,
            256L,
            131072L};


}
