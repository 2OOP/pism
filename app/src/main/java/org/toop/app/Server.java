package org.toop.app;

import com.google.common.util.concurrent.AbstractScheduledService;
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
import org.toop.framework.networking.interfaces.NetworkingClient;
import org.toop.framework.networking.types.NetworkingConnector;
import org.toop.local.AppContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Server {
	private String user = "";

	private long clientId = -1;
	private List<String> onlinePlayers = new CopyOnWriteArrayList<String>();

	private ServerView view;

	private boolean isPolling = true;

	public static GameInformation.Type gameToType(String game) {
		if (game.equalsIgnoreCase("tic-tac-toe")) {
			return GameInformation.Type.TICTACTOE;
		} else if (game.equalsIgnoreCase("reversi")) {
			return GameInformation.Type.REVERSI;
		}

		return null;
	}

	public Server(String ip, String port, String user) {
		if (ip.split("\\.").length < 4) {
			ViewStack.push(new ErrorView("\"" + ip + "\" " + AppContext.getString("is-not-a-valid-ip-address")));
			return;
		}

		int parsedPort = -1;

		try {
			parsedPort = Integer.parseInt(port);
		} catch (NumberFormatException _) {
			ViewStack.push(new ErrorView("\"" + port + "\" " + AppContext.getString("is-not-a-valid-port")));
			return;
		}

		if (user.isEmpty()) {
			ViewStack.push(new ErrorView(AppContext.getString("invalid-username")));
			return;
		}

		new EventFlow()
			.addPostEvent(NetworkEvents.StartClient.class,
					new TournamentNetworkingClient(),
					new NetworkingConnector(ip, parsedPort, 10, 1, TimeUnit.SECONDS)
			)
			.onResponse(NetworkEvents.StartClientResponse.class, e -> {
				// TODO add if unsuccessful
				this.user = user;
				clientId = e.clientId();

				new EventFlow().addPostEvent(new NetworkEvents.SendLogin(clientId, user)).postEvent();

				view = new ServerView(user, this::sendChallenge, this::disconnect);
				ViewStack.push(view);

				startPopulateScheduler();
			}).postEvent();

		new EventFlow().listen(this::handleReceivedChallenge);
	}

	private void populatePlayerList(ScheduledExecutorService scheduler, Runnable populatingTask) {

		final long DELAY = 5;

		if (!isPolling) scheduler.shutdown();
		else {
			populatingTask.run();
			scheduler.schedule(() -> populatePlayerList(scheduler, populatingTask), DELAY, TimeUnit.SECONDS);
		}
	}

	private void sendChallenge(String opponent) {
		if (!isPolling) {
			return;
		}

		ViewStack.push(new SendChallengeView(this, opponent, (playerInformation, gameType) -> {
			new EventFlow().addPostEvent(new NetworkEvents.SendChallenge(clientId, opponent, gameType))
				.listen(NetworkEvents.GameMatchResponse.class, e -> {
					if (e.clientId() == clientId) {
						isPolling = false;
						onlinePlayers.clear();

						final GameInformation.Type type = gameToType(gameType);
						final int myTurn = e.playerToMove().equalsIgnoreCase(e.opponent())? 1 : 0;

						final GameInformation information = new GameInformation(type);
						information.players[0] = playerInformation;
						information.players[0].name = user;
						information.players[1].name = opponent;

						switch (type) {
							case TICTACTOE: new TicTacToeGame(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage); break;
							case REVERSI: new ReversiGame(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage); break;
						}
					}
				}).postEvent();
		}));
	}

	private void handleReceivedChallenge(NetworkEvents.ChallengeResponse response) {
		if (!isPolling) {
			return;
		}

		String challengerName = response.challengerName();
		challengerName = challengerName.substring(challengerName.indexOf("\"") + 1);
		challengerName = challengerName.substring(0, challengerName.indexOf("\""));

		String gameType = response.gameType();
		gameType = gameType.substring(gameType.indexOf("\"") + 1);
		gameType = gameType.substring(0, gameType.indexOf("\""));

		final String finalGameType = gameType;

		ViewStack.push(new ChallengeView(challengerName, gameType, (playerInformation) -> {
			final int challengeId = Integer.parseInt(response.challengeId().substring(18, response.challengeId().length() - 2));
			new EventFlow().addPostEvent(new NetworkEvents.SendAcceptChallenge(clientId, challengeId)).postEvent();

			ViewStack.pop();

			new EventFlow().listen(NetworkEvents.GameMatchResponse.class, e -> {
				if (e.clientId() == clientId) {
					isPolling = false;
					onlinePlayers.clear();

					final GameInformation.Type type = gameToType(finalGameType);
					final int myTurn = e.playerToMove().equalsIgnoreCase(e.opponent())? 1 : 0;

					final GameInformation information = new GameInformation(type);
					information.players[0] = playerInformation;
					information.players[0].name = user;
					information.players[1].name = e.opponent();

					switch (type) {
						case TICTACTOE: new TicTacToeGame(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage); break;
						case REVERSI: new ReversiGame(information, myTurn, this::forfeitGame, this::exitGame, this::sendMessage); break;
					}
				}
			});
		}));
	}

	private void sendMessage(String message) {
		new EventFlow().addPostEvent(new NetworkEvents.SendMessage(clientId, message)).postEvent();
	}

	private void disconnect() {
		new EventFlow().addPostEvent(new NetworkEvents.CloseClient(clientId)).postEvent();
		isPolling = false;
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

		EventFlow getPlayerlistFlow = new EventFlow()
			.addPostEvent(new NetworkEvents.SendGetPlayerlist(clientId))
			.listen(NetworkEvents.PlayerlistResponse.class, e -> {
			if (e.clientId() == clientId) {
				onlinePlayers = new ArrayList<>(List.of(e.playerlist()));
				onlinePlayers.removeIf(name -> name.equalsIgnoreCase(user));

				view.update(onlinePlayers);
			}
		}, false);

		final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.schedule(() -> populatePlayerList(scheduler, getPlayerlistFlow::postEvent), 0, TimeUnit.MILLISECONDS);
	}

	public List<String> getGamesList() {
		final List<String> list = new ArrayList<String>();
		list.add("tic-tac-toe"); // Todo: get games list from server and check if the game is supported
		list.add("reversi");

		new EventFlow().addPostEvent(new NetworkEvents.SendGetGamelist(clientId))
			.listen(NetworkEvents.GamelistResponse.class, e -> {
				System.out.println(Arrays.toString(e.gamelist()));
			}).postEvent();

		return list;
	}
}