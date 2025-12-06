package org.toop.app;

import javafx.application.Platform;
import javafx.geometry.Pos;
import org.toop.app.gameControllers.*;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.LoadingWidget;
import org.toop.app.widget.popup.ChallengePopup;
import org.toop.app.widget.popup.ErrorPopup;
import org.toop.app.widget.popup.SendChallengePopup;
import org.toop.app.widget.view.ServerView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.controller.GameController;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.networking.clients.TournamentNetworkingClient;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.networking.types.NetworkingConnector;
import org.toop.game.games.reversi.BitboardReversi;
import org.toop.game.games.tictactoe.BitboardTicTacToe;
import org.toop.game.players.ArtificialPlayer;
import org.toop.game.players.OnlinePlayer;
import org.toop.game.players.RandomAI;
import org.toop.local.AppContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Server {
	private String user = "";
	private long clientId = -1;

	private final List<String> onlinePlayers = new CopyOnWriteArrayList<>();
	private final List<String> gameList = new CopyOnWriteArrayList<>();

	private ServerView primary;
	private boolean isPolling = true;

    private GameController gameController;

    private final AtomicBoolean isSingleGame = new AtomicBoolean(false);

	private ScheduledExecutorService scheduler;

	private EventFlow connectFlow;

	public static GameInformation.Type gameToType(String game) {
		if (game.equalsIgnoreCase("tic-tac-toe")) {
			return GameInformation.Type.TICTACTOE;
		} else if (game.equalsIgnoreCase("reversi")) {
			return GameInformation.Type.REVERSI;
		}

		return null;
	}


    // Server has to deal with ALL network related listen events. This "server" can then interact with the manager to make stuff happen.
    // This prevents data races where events get sent to the game manager but the manager isn't ready yet.
	public Server(String ip, String port, String user) {
		if (ip.split("\\.").length < 4) {
			new ErrorPopup("\"" + ip + "\" " + AppContext.getString("is-not-a-valid-ip-address"));
			return;
		}

		int parsedPort;

		try {
			parsedPort = Integer.parseInt(port);
		} catch (NumberFormatException _) {
			new ErrorPopup("\"" + port + "\" " + AppContext.getString("is-not-a-valid-port"));
			return;
		}

		if (user.isEmpty() || user.matches("^[0-9].*")) {
			new ErrorPopup(AppContext.getString("invalid-username"));
			return;
		}

		final int reconnectAttempts = 10;

		LoadingWidget loading = new LoadingWidget(
                Primitive.text("connecting"), 0, 0, reconnectAttempts, true, true
        );

		WidgetContainer.getCurrentView().transitionNext(loading);

		var a = new EventFlow()
			.addPostEvent(NetworkEvents.StartClient.class,
				new TournamentNetworkingClient(),
				new NetworkingConnector(ip, parsedPort, reconnectAttempts, 1, TimeUnit.SECONDS)
			);

        loading.setOnFailure(() -> {
            if (WidgetContainer.getCurrentView() == loading) WidgetContainer.getCurrentView().transitionPrevious();
			a.unsubscribeAll();
            WidgetContainer.add(
                    Pos.CENTER,
                    new ErrorPopup(AppContext.getString("connecting-failed") + " " + ip + ":" + port)
            );
        });

		a.onResponse(NetworkEvents.StartClientResponse.class, e -> {

			if (!e.successful()) {
				return;
			}

			primary = new ServerView(user, this::sendChallenge);
			WidgetContainer.getCurrentView().transitionNextCustom(primary, "disconnect", this::disconnect);

			a.unsubscribe("connecting");
			a.unsubscribe("startclient");

			this.user = user;
			clientId = e.clientId();

			new EventFlow().addPostEvent(new NetworkEvents.SendLogin(clientId, user)).postEvent();

			startPopulateScheduler();
			populateGameList();

			primary.removeViewFromPreviousChain(loading);

		}, false, "startclient")
				.listen(
                        NetworkEvents.ConnectTry.class,
                        e -> Platform.runLater(
                                () -> {
                                    try {
                                        loading.setAmount(e.amount());
                                        if (e.amount() >= loading.getMaxAmount()) {
                                            loading.triggerFailure();
                                        }
                                    } catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                        ),
                        false, "connecting"
                )
				.postEvent();

		a.listen(NetworkEvents.ChallengeResponse.class, this::handleReceivedChallenge, false, "challenge")
                .listen(NetworkEvents.GameMatchResponse.class, this::handleMatchResponse, false, "match-response")
                .listen(NetworkEvents.GameResultResponse.class, this::handleGameResult, false, "game-result")
                .listen(NetworkEvents.GameMoveResponse.class, this::handleReceivedMove, false, "game-move")
                .listen(NetworkEvents.YourTurnResponse.class, this::handleYourTurn, false, "your-turn");

		connectFlow = a;
	}

	private void sendChallenge(String opponent) {
		if (!isPolling) return;

		var a = new SendChallengePopup(this, opponent, (playerInformation, gameType) -> {
			new EventFlow().addPostEvent(new NetworkEvents.SendChallenge(clientId, opponent, gameType)).postEvent();
            isSingleGame.set(true);
		});

		a.show(Pos.CENTER);
	}

    private void handleMatchResponse(NetworkEvents.GameMatchResponse response) {
        // TODO: Redo all of this mess
        if (gameController != null) {
            gameController.stop();
        }

        gameController = null;

        //if (!isPolling) return;

        String gameType = extractQuotedValue(response.gameType());
        if (response.clientId() == clientId) {
            isPolling = false;
            onlinePlayers.clear();

            final GameInformation.Type type = gameToType(gameType);
            if (type == null) {
                new ErrorPopup("Unsupported game type: " + gameType);
                return;
            }

            final int myTurn = response.playerToMove().equalsIgnoreCase(response.opponent()) ? 1 : 0;

            final GameInformation information = new GameInformation(type);
            //information.players[0] = playerInformation;
            information.players[0].name = user;
            information.players[0].isHuman = false;
            information.players[0].computerDifficulty = 5;
            information.players[0].computerThinkTime = 1;
            information.players[1].name = response.opponent();

            /*switch (type){
                case TICTACTOE ->{
                    players[myTurn] = new ArtificialPlayer<>(new TicTacToeAIR(9), user);
                }
                case REVERSI ->{
                    players[myTurn] = new ArtificialPlayer<>(new ReversiAIR(), user);
                }
            }*/



            switch (type) {
                case TICTACTOE ->{
                        Player<BitboardTicTacToe>[] players = new Player[2];
                        players[(myTurn + 1) % 2] = new OnlinePlayer<>(response.opponent());
                        players[myTurn] = new ArtificialPlayer<>(new RandomAI<BitboardTicTacToe>(), user);
                        gameController = new TicTacToeBitController(players);
                }
                case REVERSI -> {
                    Player<BitboardReversi>[] players = new Player[2];
                    players[(myTurn + 1) % 2] = new OnlinePlayer<>(response.opponent());
                    players[myTurn] = new ArtificialPlayer<>(new RandomAI<BitboardReversi>(), user);
                    gameController = new ReversiBitController(players);}
                default -> new ErrorPopup("Unsupported game type.");

            }

            if (gameController != null){
                gameController.start();
            }
        }
    }

    private void handleYourTurn(NetworkEvents.YourTurnResponse response) {
        if (gameController == null) {
            return;
        }
        gameController.onYourTurn(response);

    }

    private void handleGameResult(NetworkEvents.GameResultResponse response) {
        if (gameController == null) {
            return;
        }
        gameController.gameFinished(response);
    }

    private void handleReceivedMove(NetworkEvents.GameMoveResponse response) {
        if (gameController == null) {
            return;
        }
        gameController.onMoveReceived(response);
    }

	private void handleReceivedChallenge(NetworkEvents.ChallengeResponse response) {
        if (!isPolling) return;

		String challengerName = extractQuotedValue(response.challengerName());
		String gameType = extractQuotedValue(response.gameType());
		final String finalGameType = gameType;
		var a = new ChallengePopup(challengerName, gameType, (playerInformation) -> {
			final int challengeId = Integer.parseInt(response.challengeId().replaceAll("\\D", ""));
			new EventFlow().addPostEvent(new NetworkEvents.SendAcceptChallenge(clientId, challengeId)).postEvent();
            isSingleGame.set(true);
		});

		a.show(Pos.CENTER);
	}

	private void sendMessage(String message) {
		new EventFlow().addPostEvent(new NetworkEvents.SendMessage(clientId, message)).postEvent();
	}

	private void disconnect() {
		new EventFlow().addPostEvent(new NetworkEvents.CloseClient(clientId)).postEvent();
		isPolling = false;
		stopScheduler();
		connectFlow.unsubscribeAll();

		WidgetContainer.getCurrentView().transitionPrevious();
	}

	private void forfeitGame() {
		new EventFlow().addPostEvent(new NetworkEvents.SendForfeit(clientId)).postEvent();
	}

	private void exitGame() {
		forfeitGame();
		startPopulateScheduler();
	}

    private void gameOver(){
        startPopulateScheduler();
    }

	private void startPopulateScheduler() {
		isPolling = true;
        isSingleGame.set(false);
		stopScheduler();

		new EventFlow()
			.listen(NetworkEvents.PlayerlistResponse.class, e -> {
				if (e.clientId() == clientId) {
					onlinePlayers.clear();
					onlinePlayers.addAll(List.of(e.playerlist()));
					onlinePlayers.removeIf(name -> name.equalsIgnoreCase(user));
					primary.update(onlinePlayers);
				}
			}, false);

		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			if (isPolling) {
				new EventFlow().addPostEvent(new NetworkEvents.SendGetPlayerlist(clientId)).postEvent();
			} else {
				stopScheduler();
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	private void stopScheduler() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdownNow();
		}
	}

	private void gamesListFromServerHandler(NetworkEvents.GamelistResponse event) {
		gameList.clear();
		gameList.addAll(List.of(event.gamelist()));
	}

	public void populateGameList() {
		new EventFlow().addPostEvent(new NetworkEvents.SendGetGamelist(clientId))
			.listen(NetworkEvents.GamelistResponse.class, this::gamesListFromServerHandler, true)
			.postEvent();
	}

	public List<String> getGameList() {
		return gameList;
	}

	private String extractQuotedValue(String s) {
		int first = s.indexOf('"');
		int last = s.lastIndexOf('"');
		if (first >= 0 && last > first) {
			return s.substring(first + 1, last);
		}
		return s;
	}
}