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
		App.run(args);

		// final ExecutorService executor = Executors.newFixedThreadPool(1);
		// executor.execute(() -> testAIs(25));
    }
}