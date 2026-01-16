package org.toop;


import org.toop.app.App;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.game.players.ArtificialPlayer;
import org.toop.game.players.ai.MCTSAI;
import org.toop.game.players.ai.RandomAI;
import org.toop.game.players.ai.mcts.MCTSAI1;
import org.toop.game.players.ai.mcts.MCTSAI2;
import org.toop.game.players.ai.mcts.MCTSAI3;
import org.toop.game.players.ai.mcts.MCTSAI4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Main {
    static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(1);

		executor.execute(() -> testMCTS(25));
		App.run(args);
    }

	private static void testMCTS(int games) {
		var versions = new ArtificialPlayer[5];
		versions[0] = new ArtificialPlayer(new RandomAI(), "Random AI");
		versions[1] = new ArtificialPlayer(new MCTSAI1(1000), "MCTS V1 AI");
		versions[2] = new ArtificialPlayer(new MCTSAI2(1000), "MCTS V2 AI");
		versions[3] = new ArtificialPlayer(new MCTSAI3(10, 10), "MCTS V3 AI");
		versions[4] = new ArtificialPlayer(new MCTSAI4(10, 10), "MCTS V4 AI");

		for (int i = 0; i < versions.length; i++) {
			for (int j = i + 1; j < versions.length; j++) {
				final int playerIndex1 = i % versions.length;
				final int playerIndex2 = j % versions.length;

				testAI(games, new ArtificialPlayer[] { versions[playerIndex1], versions[playerIndex2]});
			}
		}
	}

	private static void testAI(int games, ArtificialPlayer[] ais) {
		int wins = 0;
		int ties = 0;

		for (int i = 0; i < games; i++) {
			final BitboardReversi match = new BitboardReversi();
			match.init(ais);

			while (!match.isTerminal()) {
				final int currentAI = match.getCurrentTurn();
				final long move = ais[currentAI].getMove(match);

				match.play(move);

				if (ais[currentAI].getAi() instanceof MCTSAI mcts) {
					final int lastIterations = mcts.getLastIterations();
					System.out.printf("iterations %s: %d\n", ais[currentAI].getName(), lastIterations);
				}
			}

			if (match.getWinner() < 0) {
				ties++;
				continue;
			}

			wins += match.getWinner() == 0? 1 : 0;
		}

		System.out.printf("Out of %d games, %s won %d -- tied %d -- lost %d, games against %s\n", games, ais[0].getName(), wins, ties, games - wins - ties, ais[1].getName());
		System.out.printf("Average win rate was: %.2f\n\n", wins / (float)games);
	}
}