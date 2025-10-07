package org.toop.app.layer.layers;

import javafx.application.Platform;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.Popup;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.app.layer.layers.game.TicTacToeLayer;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConnectedLayer extends Layer {
	private static Timer pollTimer = new Timer();

	private static class ChallengePopup extends Popup {
		private final GameInformation information;

		private final String challenger;
		private final String game;

		private final long clientID;
		private final int challengeID;

		public ChallengePopup(GameInformation information, String challenger, String game, long clientID, String challengeID) {
			super(false, "bg-popup");

			this.information = information;

			this.challenger = challenger;
			this.game = game;

			this.clientID = clientID;
			this.challengeID = Integer.parseInt(challengeID.substring(18, challengeID.length() - 2));

			reload();
		}

		@Override
		public void reload() {
			popAll();

			final var challengeText = NodeBuilder.header(AppContext.getString("challengeText"));
			final var challengerNameText = NodeBuilder.header(challenger);

			final var gameText = NodeBuilder.text(AppContext.getString("gameIsText"));
			final var gameNameText = NodeBuilder.text(game);

			final var acceptButton = NodeBuilder.button(AppContext.getString("accept"), () -> {
				pollTimer.cancel();

				new EventFlow().addPostEvent(new NetworkEvents.SendAcceptChallenge(clientID, challengeID)).postEvent();
				App.activate(new TicTacToeLayer(information, clientID));
			});

			final var denyButton = NodeBuilder.button(AppContext.getString("deny"), () -> {
				App.pop();
			});

			final Container controlContainer = new HorizontalContainer(30);
			controlContainer.addNodes(acceptButton, denyButton);

			final Container mainContainer = new VerticalContainer(30);
			mainContainer.addNodes(challengeText, challengerNameText);
			mainContainer.addNodes(gameText, gameNameText);

			mainContainer.addContainer(controlContainer, false);

			addContainer(mainContainer, Pos.CENTER, 0, 0, 30, 30);
		}
	}

	GameInformation information;
	long clientId;
	String user;
	List<String> onlinePlayers = new CopyOnWriteArrayList<>();

	public ConnectedLayer(GameInformation information) {
		super("bg-primary");

		this.information = information;

		new EventFlow()
				.addPostEvent(NetworkEvents.StartClient.class, information.serverIP(), Integer.parseInt(information.serverPort()))
				.onResponse(NetworkEvents.StartClientResponse.class, e -> {
					clientId = e.clientId();
					user = information.playerName()[0].replaceAll("\\s+", "");

					new EventFlow().addPostEvent(new NetworkEvents.SendLogin(this.clientId, this.user)).postEvent();

					Thread popThread = new Thread(this::populatePlayerList);
					popThread.setDaemon(false);
					popThread.start();
				}).postEvent();

		new EventFlow().listen(this::handleReceivedChallenge);

        reload();
	}

	private void populatePlayerList() {
		EventFlow sendGetPlayerList = new EventFlow().addPostEvent(new NetworkEvents.SendGetPlayerlist(this.clientId));
		new EventFlow().listen(NetworkEvents.PlayerlistResponse.class, e -> {
			if (e.clientId() == this.clientId) {
				List<String> playerList = new java.util.ArrayList<>(List.of(e.playerlist())); // TODO: Garbage, but works
				playerList.removeIf(name -> name.equalsIgnoreCase(user));
				if (this.onlinePlayers != playerList) {
					this.onlinePlayers.clear();
					this.onlinePlayers.addAll(playerList);
				}
			}
		});

		TimerTask task = new TimerTask() {
			public void run() {
				sendGetPlayerList.postEvent();
                Platform.runLater(() -> reload());
			}
		};

		pollTimer.schedule(task, 0L, 5000L); // TODO: Block app exit, fix later
	}

	private void sendChallenge(String oppUsername, String gameType) {
		final AtomicInteger challengeId = new AtomicInteger(-1);

		if (onlinePlayers.contains(oppUsername)) {
			new EventFlow().addPostEvent(new NetworkEvents.SendChallenge(this.clientId, oppUsername, gameType))
					.listen(NetworkEvents.ChallengeResponse.class, e -> {
						challengeId.set(Integer.parseInt(e.challengeId().substring(18, e.challengeId().length() - 2)));
					})
					.listen(NetworkEvents.GameMatchResponse.class, e -> {
						if (e.clientId() == this.clientId) {
							pollTimer.cancel();
							App.activate(new TicTacToeLayer(information, this.clientId));
						}
					}, false).postEvent();
			//           ^
 			//           |
 			//           |
 			//           |
		}
	}

	private void handleReceivedChallenge(NetworkEvents.ChallengeResponse response) {
		App.push(new ChallengePopup(information, response.challengerName(), response.gameType(), clientId, response.challengeId()));
	}

	@Override
	public void reload() {
		popAll();

		ListView<Label> players = new ListView<>();

		for (int i = 0; i < onlinePlayers.size(); i++) {
            int finalI = i;
            players.getItems().add(NodeBuilder.button(onlinePlayers.get(i), () -> {
                String clickedPlayer = onlinePlayers.get(finalI);
                sendChallenge(clickedPlayer, "tic-tac-toe");
			}));
		}

		final Container playersContainer = new VerticalContainer(10);
		playersContainer.addNodes(players);

		addContainer(playersContainer, Pos.CENTER, 0, 0, 0, 0);
	}
}