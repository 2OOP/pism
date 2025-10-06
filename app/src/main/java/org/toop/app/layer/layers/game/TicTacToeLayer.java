package org.toop.app.layer.layers.game;

import javafx.scene.text.Text;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.containers.HorizontalContainer;
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
import java.util.concurrent.atomic.AtomicBoolean;

public final class TicTacToeLayer extends Layer {
	private TicTacToeCanvas canvas;

	private TicTacToe ticTacToe;
	private TicTacToeAI ticTacToeAI;

	private GameInformation information;

	private final Text currentPlayerNameText;
	private final Text currentPlayerMoveText;

	private final BlockingQueue<Game.Move> playerMoveQueue = new LinkedBlockingQueue<>();

	// Todo: set these from the server
	private char currentPlayerMove = Game.EMPTY;
	private String player2Name = "";

	public TicTacToeLayer(GameInformation information) {
		super("bg-primary");

		canvas = new TicTacToeCanvas(Color.LIME, (App.getHeight() / 100) * 75, (App.getHeight() / 100) * 75, (cell) -> {
			try {
				if (information.isConnectionLocal()) {
					if (ticTacToe.getCurrentTurn() == 0) {
						playerMoveQueue.put(new Game.Move(cell, 'X'));
					} else {
						playerMoveQueue.put(new Game.Move(cell, 'O'));
					}
				} else {
                    if (information.isPlayerHuman()[0] && currentPlayerMove != Game.EMPTY) {
                        playerMoveQueue.put(new Game.Move(cell, currentPlayerMove));
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

		currentPlayerNameText = NodeBuilder.header("");
		currentPlayerMoveText = NodeBuilder.header("");

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

		final var backButton = NodeBuilder.button(AppContext.getString("back"), () -> {
			App.activate(new MainLayer());
		});

		final Container controlContainer = new VerticalContainer(5);
		controlContainer.addNodes(backButton);

		final Container informationContainer = new HorizontalContainer(15);
		informationContainer.addNodes(currentPlayerNameText, currentPlayerMoveText);

		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
		addContainer(informationContainer, Pos.TOP_LEFT, 2, 2, 0, 0);
		addGameCanvas(canvas, Pos.CENTER, 0, 0);
	}

	private int compurterDifficultyToDepth(int maxDifficulty, int difficulty) {
		return (int) (((float) maxDifficulty / difficulty) * 9);
	}

	private void localGameThread() {
		boolean running = true;

		while (running) {
			final int currentPlayer = ticTacToe.getCurrentTurn();

			currentPlayerNameText.setText(information.playerName()[currentPlayer]);
			currentPlayerMoveText.setText(ticTacToe.getCurrentTurn() == 0? "X" : "O");

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
					App.push(new GameFinishedPopup(false, information.playerName()[ticTacToe.getCurrentTurn()]));
				} else if (state == Game.State.DRAW) {
					App.push(new GameFinishedPopup(true, ""));
				}

				running = false;
			}
		}
	}

	class OnlineGameState {
		public long clientId = -1;
		public long receivedMove = -1;
        public boolean firstPlayerIsMe = true;
	}
    AtomicBoolean firstPlayerIsMe = new AtomicBoolean(true);
    AtomicBoolean gameHasStarted = new AtomicBoolean(false);
	private void serverGameThread(NetworkEvents.StartClientResponse event) {
		boolean running = true;
		final long clientId = event.clientId();
		final OnlineGameState onlineGameState = new OnlineGameState();
		onlineGameState.clientId = clientId;

		//new EventFlow()
		//		.listen(NetworkEvents.GameMoveResponse.class,respEvent -> onMoveResponse(onlineGameState, respEvent));

        new EventFlow()
                .listen(this::yourTurnResponse)
                .listen(this::handleChallengeResponse)
                .listen(this::handleServerGameStart)
                .listen(this::handleReceivedMessage)
                .listen(this::onMoveResponse);

		while (running) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException exception) {}
            boolean hasStarted = gameHasStarted.get();
            if (hasStarted) {
                onlineGameState.firstPlayerIsMe = firstPlayerIsMe.get();
                if (onlineGameState.firstPlayerIsMe) {
                    currentPlayerMove = 'X';
                }
                else {
                    currentPlayerMove = 'O';
                }
                if(!information.isPlayerHuman()[0]){
                    boolean myTurn = (onlineGameState.firstPlayerIsMe && ticTacToe.getCurrentTurn() % 2 == 0)
                            || (!onlineGameState.firstPlayerIsMe && ticTacToe.getCurrentTurn() % 2 == 1);
                    if (myTurn) {
                        Game.Move move;
                        move = ticTacToeAI.findBestMove(ticTacToe, compurterDifficultyToDepth(10, 10));
                        new EventFlow().addPostEvent(new NetworkEvents.SendMove(clientId, (short) move.position()))
                                .postEvent();
                    }
                }
                else {
                    try {
                        final Game.Move wants = playerMoveQueue.take();
                        final Game.Move[] legalMoves = ticTacToe.getLegalMoves();
                        for (final Game.Move legalMove : legalMoves) {
                            if (legalMove.position() == wants.position() && legalMove.value() == wants.value()) {
                                new EventFlow().addPostEvent(new NetworkEvents.SendMove(clientId, (short) wants.position()))
                                        .postEvent();
                                break;
                            }
                        }
                    } catch (InterruptedException exception) {
                        System.out.println(exception.getMessage());
                        return;
                    }
                }
            }
		}
	}

    private void drawSymbol(Game.Move move) {
        if (move.value() == 'X') {
            canvas.drawX(Color.RED, move.position());
        } else if (move.value() == 'O') {
            canvas.drawO(Color.BLUE, move.position());
        }
    }

    private void handleServerGameStart(NetworkEvents.GameMatchResponse resp) {
        if(resp.playerToMove().equals(resp.opponent())){
            firstPlayerIsMe.set(false);
        }
        else{
            firstPlayerIsMe.set(true);
        }
        gameHasStarted.set(true);
    }

	private void onMoveResponse(NetworkEvents.GameMoveResponse resp) {
        char playerChar;
        if (resp.player().equals(information.playerName()[0]) && firstPlayerIsMe.get()
                || !resp.player().equals(information.playerName()[0]) && !firstPlayerIsMe.get()) {
            playerChar = 'X';
        }
        else {
            playerChar = 'O';
        }
        Game.Move move =new Game.Move(Integer.parseInt(resp.move()),playerChar);
        Game.State state = ticTacToe.play(move);
        if (state != Game.State.NORMAL) { //todo differentiate between future draw guaranteed and is currently a draw
            gameHasStarted.set(false);
        }
        drawSymbol(move);
	}

    private void handleChallengeResponse(NetworkEvents.ChallengeResponse resp) {
        new EventFlow().addPostEvent(new NetworkEvents.SendAcceptChallenge(resp.clientId(),Integer.parseInt(resp.challengeId())))
                .postEvent();
    }

    private void yourTurnResponse(NetworkEvents.YourTurnResponse response) {

        //new EventFlow().addPostEvent(new NetworkEvents.SendCommand(response.clientId(),"CHALLENGE banaan tic-tac-toe")).postEvent();
        //new EventFlow().addPostEvent(new NetworkEvents.SendMove(response.clientId(),(short)2))
        //        .postEvent();
    }
    private void handleReceivedMessage(NetworkEvents.ReceivedMessage msg) {
        System.out.println("Received Message: " + msg.message()); //todo add chat window
    }

	private void serverGameThreadResponseHandler(OnlineGameState ogs, NetworkEvents.ChallengeResponse msg) {
		if (msg.clientId() != ogs.clientId) return;
		IO.println("Client ID: " + ogs.clientId + " Received Message: " + msg);
	}

}