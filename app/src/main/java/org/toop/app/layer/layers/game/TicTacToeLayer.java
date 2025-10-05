package org.toop.app.layer.layers.game;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.app.layer.layers.MainLayer;
import org.toop.game.Game;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.game.tictactoe.TicTacToeAI;
import org.toop.local.AppContext;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class TicTacToeLayer extends Layer {
	private TicTacToeCanvas canvas;

	private TicTacToe ticTacToe;
	private TicTacToeAI ticTacToeAI;

	private GameInformation information;

	private final BlockingQueue<Game.Move> playerMoveQueue = new LinkedBlockingQueue<>();

	public TicTacToeLayer(GameInformation information) {
		super("game.css");

		canvas = new TicTacToeCanvas(Color.WHITE, (App.getHeight() / 100) * 75, (App.getHeight() / 100) * 75, (cell) -> {
			try {
				playerMoveQueue.put(new Game.Move(cell, 'X'));
			} catch (InterruptedException e) {
				return;
			}
		});

		ticTacToe = new TicTacToe();
		ticTacToeAI = new TicTacToeAI();

		this.information = information;

		if (information.isConnectionLocal()) {
			new Thread(this::localGameThread).start();
		}

		reload();
	}

	@Override
	public void reload() {
		popAll();

		canvas.resize((App.getHeight() / 100) * 75, (App.getHeight() / 100) * 75);

		for (int i = 0; i < ticTacToe.board.length; i++) {
			final char value = ticTacToe.board[i];

			if (value == 'X') {
				canvas.drawX(Color.RED, i);
			} else if (value == 'O') {
				canvas.drawO(Color.BLUE, i);
			}
		}

		final Container controlContainer = new VerticalContainer(5);

		if (information.isPlayerHuman()[0] || information.isConnectionLocal() && information.isPlayerHuman()[1]) {
			controlContainer.addButton(AppContext.getString("hint"), () -> {
			});
		}

		controlContainer.addButton(AppContext.getString("back"), () -> {
			App.activate(new MainLayer());
		});

		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
		addGameCanvas(canvas, Pos.CENTER, 0, 0);
	}

	private int compurterDifficultyToDepth(int maxDifficulty, int difficulty) {
		return (int) (((float) maxDifficulty / difficulty) * 9);
	}

	private void localGameThread() {
		boolean running = true;

		while (running) {
			final int currentPlayer = ticTacToe.getCurrentTurn();

			System.out.println("test");

			Game.Move move = null;

			if (information.isPlayerHuman()[currentPlayer]) {
				try {
					move = playerMoveQueue.take();
				} catch (InterruptedException exception) {
					return;
				}
			} else {
				move = ticTacToeAI.findBestMove(ticTacToe, compurterDifficultyToDepth(9, information.computerDifficulty()[currentPlayer]));
			}

			assert move != null;
			final Game.State state = ticTacToe.play(move);

			if (move.value() == 'X') {
				canvas.drawX(Color.RED, move.position());
			} else if (move.value() == 'O') {
				canvas.drawO(Color.BLUE, move.position());
			}

			if (state != Game.State.NORMAL) {
				if (state == Game.State.WIN) {
					// Win logic
				} else if (state == Game.State.DRAW) {
					// Draw logic
				}

				running = false;
			}
		}
	}
}