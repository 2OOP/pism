package org.toop.app;

import org.toop.app.game.Connect4Game;
import org.toop.app.game.ReversiGame;
import org.toop.app.game.TicTacToeGame;
import org.toop.app.view.ViewStack;
import org.toop.app.view.views.ChallengeView;
import org.toop.app.view.views.ErrorView;
import org.toop.app.view.views.OnlineView;
import org.toop.app.view.views.SendChallengeView;
import org.toop.app.view.views.ServerView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.clients.TournamentNetworkingClient;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.networking.types.NetworkingConnector;
import org.toop.local.AppContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Server {
	private String user = "";
	private long clientId = -1;

	private final List<String> onlinePlayers = new CopyOnWriteArrayList<>();
	private final List<String> gameList = new CopyOnWriteArrayList<>();

	private ServerView view;
	private boolean isPolling = true;

	private ScheduledExecutorService scheduler;

	public static GameInformation.Type gameToType(String game) {
		if (game.equalsIgnoreCase("tic-tac-toe")) {
			return GameInformation.Type.TICTACTOE;
		} else if (game.equalsIgnoreCase("reversi")) {
			return GameInformation.Type.REVERSI;
		} else if (game.equalsIgnoreCase("connect4")) {
			return GameInformation.Type.CONNECT4;
//		} else if (game.equalsIgnoreCase("battleship")) {
//			return GameInformation.Type.BATTLESHIP;
		}

		return null;
	}

	public Server(String ip, String port, String user) {
		if (ip.split("\\.").length < 4) {
			ViewStack.push(new ErrorView("\"" + ip + "\" " + AppContext.getString("is-not-a-valid-ip-address")));
			return;
		}

		int parsedPort;

		try {
			parsedPort = Integer.parseInt(port);
		} catch (NumberFormatException _) {
			ViewStack.push(new ErrorView("\"" + port + "\" " + AppContext.getString("is-not-a-valid-port")));
			return;
		}

		if (user.isEmpty() || user.matches("^[0-9].*")) {
			ViewStack.push(new ErrorView(AppContext.getString("invalid-username")));
			return;
		}

		new EventFlow()
			.addPostEvent(NetworkEvents.StartClient.class,
				new TournamentNetworkingClient(),
				new NetworkingConnector(ip, parsedPort, 10, 1, TimeUnit.SECONDS)
			)
			.onResponse(NetworkEvents.StartClientResponse.class, e -> {
				this.user = user;
				clientId = e.clientId();

				new EventFlow().addPostEvent(new NetworkEvents.SendLogin(clientId, user)).postEvent();

				view = new ServerView(user, this::sendChallenge, this::disconnect);
				ViewStack.push(view);

				startPopulateScheduler();
				populateGameList();

			}).postEvent();

		new EventFlow().listen(this::handleReceivedChallenge)
                .listen(this::handleMatchResponse);
	}

	private void sendChallenge(String opponent) {
		if (!isPolling) return;

		ViewStack.push(new SendChallengeView(this, opponent, (playerInformation, gameType) -> {
			new EventFlow().addPostEvent(new NetworkEvents.SendChallenge(clientId, opponent, gameType))
				.listen(NetworkEvents.GameMatchResponse.class, e -> {
					if (e.clientId() == clientId) {
						isPolling = false;
						onlinePlayers.clear();

						final GameInformation.Type type = gameToType(gameType);
						if (type == null) {
							ViewStack.push(new ErrorView("Unsupported game type: " + gameType));
							return;
						}

						final int myTurn = e.playerToMove().equalsIgnoreCase(e.opponent()) ? 1 : 0;

						final GameInformation information = new GameInformation(type);
						information.players[0] = playerInformation;
						information.players[0].name = user;
						information.players[1].name = opponent;

						switch (type) {
							case TICTACTOE -> new TicTacToeGame(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage);
							case REVERSI -> new ReversiGame(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage);
							case CONNECT4 -> new Connect4Game(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage);
							default -> ViewStack.push(new ErrorView("Unsupported game type."));
						}
					}
				}).postEvent();
		}));
	}

    private void handleMatchResponse(NetworkEvents.GameMatchResponse response) {
        if (!isPolling) return;

        String gameType = extractQuotedValue(response.gameType());

        if (response.clientId() == clientId) {
            isPolling = false;
            onlinePlayers.clear();

            final GameInformation.Type type = gameToType(gameType);
            if (type == null) {
                ViewStack.push(new ErrorView("Unsupported game type: " + gameType));
                return;
            }

            final int myTurn = response.playerToMove().equalsIgnoreCase(response.opponent()) ? 1 : 0;

            final GameInformation information = new GameInformation(type);
            //information.players[0] = playerInformation;
            information.players[0].name = user;
            information.players[0].isHuman = false;
            information.players[0].computerDifficulty = 5;
            information.players[1].name = response.opponent();

            switch (type) {
                case TICTACTOE ->
                        new TicTacToeGame(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage);
                case REVERSI ->
                        new ReversiGame(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage);
                case CONNECT4 ->
                        new Connect4Game(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage);
                default -> ViewStack.push(new ErrorView("Unsupported game type."));
            }
        }
    }

	private void handleReceivedChallenge(NetworkEvents.ChallengeResponse response) {
        if (!isPolling) return;

		String challengerName = extractQuotedValue(response.challengerName());
		String gameType = extractQuotedValue(response.gameType());
		final String finalGameType = gameType;

		ViewStack.push(new ChallengeView(challengerName, gameType, (playerInformation) -> {
			final int challengeId = Integer.parseInt(response.challengeId().replaceAll("\\D", ""));
			new EventFlow().addPostEvent(new NetworkEvents.SendAcceptChallenge(clientId, challengeId)).postEvent();
			ViewStack.pop();

			//new EventFlow().listen(NetworkEvents.GameMatchResponse.class, e -> {


			//});
		}));
	}

	private void sendMessage(String message) {
		new EventFlow().addPostEvent(new NetworkEvents.SendMessage(clientId, message)).postEvent();
	}

	private void disconnect() {
		new EventFlow().addPostEvent(new NetworkEvents.CloseClient(clientId)).postEvent();
		isPolling = false;
		stopScheduler();
		ViewStack.push(new OnlineView());
	}

	private void forfeitGame() {
		new EventFlow().addPostEvent(new NetworkEvents.SendForfeit(clientId)).postEvent();
	}

	private void exitGame() {
		forfeitGame();
		ViewStack.push(view);
		startPopulateScheduler();
	}

	private void startPopulateScheduler() {
		isPolling = true;
		stopScheduler();

		new EventFlow()
			.listen(NetworkEvents.PlayerlistResponse.class, e -> {
				if (e.clientId() == clientId) {
					onlinePlayers.clear();
					onlinePlayers.addAll(List.of(e.playerlist()));
					onlinePlayers.removeIf(name -> name.equalsIgnoreCase(user));
					view.update(onlinePlayers);
				}
			}, false);

		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			if (isPolling) {
				new EventFlow().addPostEvent(new NetworkEvents.SendGetPlayerlist(clientId)).postEvent();
			} else {
				stopScheduler();
			}
		}, 0, 5, TimeUnit.SECONDS);
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