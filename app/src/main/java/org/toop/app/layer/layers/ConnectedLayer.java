package org.toop.app.layer.layers;

import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConnectedLayer extends Layer {
	long clientId;
	String user;
	List<String> onlinePlayers = new CopyOnWriteArrayList<>();

	public ConnectedLayer(long clientId, String user) {
		super("bg-primary");

		this.clientId = clientId;
		this.user = user;
		reload();

		new EventFlow().addPostEvent(new NetworkEvents.SendLogin(this.clientId, this.user)).postEvent();
		new EventFlow().listen(this::handleReceivedChallenge);

		new Thread(this::populatePlayerList).start();
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
			}
		};

		Timer pollTimer = new Timer();
		pollTimer.schedule(task, 0L, 5000L);
	}

	private void sendChallenge(String oppUsername, String gameType) {
		if (onlinePlayers.contains(oppUsername)) {
			new EventFlow().addPostEvent(new NetworkEvents.SendChallenge(this.clientId, oppUsername, gameType))
					.postEvent();
		}
	}

	private void handleReceivedChallenge(NetworkEvents.ChallengeResponse response) {
		// TODO: Popup? Idk what this actually sends back.
	}

	@Override
	public void reload() {
		popAll();

		ListView<Label> players = new ListView<>();

		for (int i = 0; i < onlinePlayers.size(); i++) {
			players.getItems().add(NodeBuilder.button(onlinePlayers.get(i), () -> {
				// get clicked player name with: onlinePlayers.get(i)
				// connection logic here
			}));
		}

		final Container playersContainer = new VerticalContainer(10);
		playersContainer.addNodes(players);

		addContainer(playersContainer, Pos.CENTER, 0, 0, 0, 0);
	}
}