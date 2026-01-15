package org.toop;

import org.toop.app.App;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.games.reversi.BitboardReversi;
import org.toop.game.games.tictactoe.BitboardTicTacToe;
import org.toop.game.players.ArtificialPlayer;
import org.toop.game.players.ai.MCTSAI1;
import org.toop.game.players.ai.MCTSAI2;
import org.toop.game.players.ai.MCTSAI3;
import org.toop.game.players.ai.MCTSAI4;
import org.toop.game.players.ai.MCTSAI5;

public final class Main {
    static void main(String[] args) {
        App.run(args);
		// testMCTS(100);
    }

	private static void testMCTS(int games) {
		var versions = new ArtificialPlayer[5];
		versions[0] = new ArtificialPlayer<>(new MCTSAI1<BitboardTicTacToe>(10), "MCTS V1 AI");
		versions[1] = new ArtificialPlayer<>(new MCTSAI2<BitboardTicTacToe>(10), "MCTS V2 AI");
		versions[2] = new ArtificialPlayer<>(new MCTSAI3<BitboardTicTacToe>(10, 10), "MCTS V3 AI");
		versions[3] = new ArtificialPlayer<>(new MCTSAI4<BitboardTicTacToe>(10, 10), "MCTS V4 AI");
		versions[4] = new ArtificialPlayer<>(new MCTSAI5<BitboardTicTacToe>(10, 10), "MCTS V5 AI");

		for (int i = 2; i < versions.length; i++) {
			for (int j = i + 1; j < versions.length; j++) {
				final int playerIndex1 = i % versions.length;
				final int playerIndex2 = j % versions.length;

				testAI(games, new Player[] { versions[playerIndex1], versions[playerIndex2]});
			}
		}
	}

	private static void testAI(int games, Player<BitboardReversi>[] ais) {
		int wins = 0;
		int ties = 0;

		for (int i = 0; i < games; i++) {
			final BitboardReversi match = new BitboardReversi(ais);

			while (!match.isTerminal()) {
				final int currentAI = match.getCurrentTurn();
				final long move = ais[currentAI].getMove(match);

				match.play(move);
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