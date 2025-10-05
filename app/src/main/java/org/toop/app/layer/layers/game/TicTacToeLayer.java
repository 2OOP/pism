package org.toop.app.layer.layers.game;

import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.app.layer.layers.MainLayer;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.Game;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.game.tictactoe.TicTacToeAI;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class TicTacToeLayer extends Layer {
	private TicTacToeCanvas canvas;

	private TicTacToe ticTacToe;
	private TicTacToeAI ticTacToeAI;

	private GameInformation information;

	private final BlockingQueue<Game.Move> playerMoveQueue = new LinkedBlockingQueue<>();

	// Todo: set these from the server
	private char currentPlayerMove = Game.EMPTY;
	private String player2Name = "";

	public TicTacToeLayer(GameInformation information) {
		super("game.css");

		canvas = new TicTacToeCanvas(Color.WHITE, (App.getHeight() / 100) * 75, (App.getHeight() / 100) * 75, (cell) -> {
			try {
				if (information.isConnectionLocal()) {
					if (ticTacToe.getCurrentTurn() == 0) {
						playerMoveQueue.put(new Game.Move(cell, 'X'));
					} else {
						playerMoveQueue.put(new Game.Move(cell, 'O'));
					}
				} else {
					if (ticTacToe.getCurrentTurn() == 0) {
						if (information.isPlayerHuman()[0] && currentPlayerMove != Game.EMPTY) {
							playerMoveQueue.put(new Game.Move(cell, currentPlayerMove));
						}
					}
				}
			} catch (InterruptedException e) {
				return;
			}
		});

		ticTacToe = new TicTacToe();
		ticTacToeAI = new TicTacToeAI();

		this.information = information;

		if (information.isConnectionLocal()) {
			new Thread(this::localGameThread).start();
		} else {
			new EventFlow()
					.addPostEvent(NetworkEvents.StartClient.class,
							information.serverIP(),
							Integer.parseInt(information.serverPort()))
					.onResponse(NetworkEvents.StartClientResponse.class, event ->
							new Thread(() -> serverGameThread(event)).start())
					.postEvent();
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

			Game.Move move = null;

			if (information.isPlayerHuman()[currentPlayer]) {
				try {
					final Game.Move wants = playerMoveQueue.take();
					final Game.Move[] legalMoves = ticTacToe.getLegalMoves();

					for (final Game.Move legalMove : legalMoves) {
						if (legalMove.position() == wants.position() && legalMove.value() == wants.value()) {
							move = wants;
						}
					}
				} catch (InterruptedException exception) {
					return;
				}
			} else {
				move = ticTacToeAI.findBestMove(ticTacToe, compurterDifficultyToDepth(10,
						information.computerDifficulty()[currentPlayer]));
			}

			if (move == null) {
				continue;
			}

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

	class OnlineGameState {
		public long clientId = -1;
		public long receivedMove = -1;
	}

	private void serverGameThread(NetworkEvents.StartClientResponse event) {
		boolean running = true;
		final long clientId = event.clientId();
		final OnlineGameState onlineGameState = new OnlineGameState();
		onlineGameState.clientId = clientId;

		new EventFlow()
				.listen(NetworkEvents.GameMoveResponse.class,respEvent -> onMoveResponse(onlineGameState, respEvent));

		new EventFlow().addPostEvent(new NetworkEvents.SendLogin(clientId, information.playerName()[0]))
				.postEvent();

		new EventFlow().addPostEvent(new NetworkEvents.SendSubscribe(clientId, "tic-tac-toe"))
				.postEvent();

		while (running) {
			final int currentPlayer = ticTacToe.getCurrentTurn();
		}
	}

	private void onMoveResponse(OnlineGameState ogs, NetworkEvents.GameMoveResponse resp) {
	}

	private void serverGameThreadResponseHandler(OnlineGameState ogs, NetworkEvents.ChallengeResponse msg) {
		if (msg.clientId() != ogs.clientId) return;
		IO.println("Client ID: " + ogs.clientId + " Received Message: " + msg);
	}

}