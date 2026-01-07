package org.toop;

import org.toop.app.App;
import org.toop.framework.gameFramework.model.player.AbstractPlayer;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.games.reversi.BitboardReversi;
import org.toop.game.games.tictactoe.BitboardTicTacToe;
import org.toop.game.players.ArtificialPlayer;
import org.toop.game.players.ai.MCTSAI;
import org.toop.game.players.ai.MCTSAI2;
import org.toop.game.players.ai.MCTSAI3;
import org.toop.game.players.ai.RandomAI;

public final class Main {
    static void main(String[] args) {
		App.run(args);
		// testMCTS(10);
    }

	// Voor onderzoek
	// private static void testMCTS(int games) {
	// 	var random = new ArtificialPlayer<>(new RandomAI<BitboardReversi>(), "Random AI");
	// 	var v1 = new ArtificialPlayer<>(new MCTSAI<BitboardTicTacToe>(10), "MCTS V1 AI");
	// 	var v2 = new ArtificialPlayer<>(new MCTSAI2<BitboardTicTacToe>(10), "MCTS V2 AI");
	// 	var v2_2 = new ArtificialPlayer<>(new MCTSAI2<BitboardTicTacToe>(100), "MCTS V2_2 AI");
	// 	var v3 = new ArtificialPlayer<>(new MCTSAI3<BitboardTicTacToe>(10), "MCTS V3 AI");

	// 	testAI(games, new Player[]{ v1, v2 });
	// 	// testAI(games, new Player[]{ v1, v3 });

	// 	// testAI(games, new Player[]{ random, v3 });
	// 	// testAI(games, new Player[]{ v2, v3 });
	// 	testAI(games, new Player[]{ v2, v3 });
	// 	// testAI(games, new Player[]{ v3, v2 });
	// }

	// private static void testAI(int games, Player<BitboardReversi>[] ais) {
	// 	int wins = 0;
	// 	int ties = 0;

	// 	for (int i = 0; i < games; i++) {
	// 		final BitboardReversi match = new BitboardReversi(ais);

	// 		while (!match.isTerminal()) {
	// 			final int currentAI = match.getCurrentTurn();
	// 			final long move = ais[currentAI].getMove(match);

	// 			match.play(move);
	// 		}

	// 		if (match.getWinner() < 0) {
	// 			ties++;
	// 			continue;
	// 		}

	// 		wins += match.getWinner() == 0? 1 : 0;
	// 	}

	// 	System.out.printf("Out of %d games, %s won %d -- tied %d -- lost %d, games against %s\n", games, ais[0].getName(), wins, ties, games - wins - ties, ais[1].getName());
	// 	System.out.printf("Average win rate was: %.2f\n\n", wins / (float)games);
	// }
}
