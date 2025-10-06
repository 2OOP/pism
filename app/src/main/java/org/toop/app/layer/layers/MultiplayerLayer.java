package org.toop.app.layer.layers;

import javafx.application.Platform;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.app.layer.layers.game.TicTacToeLayer;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.local.AppContext;

import javafx.geometry.Pos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class MultiplayerLayer extends Layer {
	private boolean isConnectionLocal = true;

	private boolean isPlayer1Human = true;
	private String player1Name = "";
	private int computer1Difficulty = 0;

	private boolean isPlayer2Human = true;
	private String player2Name = "";
	private int computer2Difficulty = 0;

	private String serverIP = "";
	private String serverPort = "";
    private long clientId = -1;

	public MultiplayerLayer() {
		super("multiplayer.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container mainContainer = new VerticalContainer(5);

		mainContainer.addToggle(AppContext.getString("local"), AppContext.getString("server"), !isConnectionLocal, (server) -> {
			isConnectionLocal = !server;
			reload();
		});

		final Container playersContainer = new HorizontalContainer(50);

		mainContainer.addContainer(playersContainer, true);

		final Container player1Container = new VerticalContainer("player_container", 5);

		playersContainer.addContainer(player1Container, true);

		playersContainer.addText("VS", false);

		final Container player2Container = new VerticalContainer("player_container", 5);

		playersContainer.addContainer(player2Container, true);

		mainContainer.addButton(isConnectionLocal? AppContext.getString("start") : AppContext.getString("connect"), () -> {
//			App.activate(new TicTacToeLayer(new GameInformation(
//					new String[] { player1Name, player2Name },
//					new boolean[] { isPlayer1Human, isPlayer2Human },
//					new int[] { computer1Difficulty, computer2Difficulty },
//					isConnectionLocal, "127.0.0.1", "7789")));

            new EventFlow()
                    .addPostEvent(NetworkEvents.StartClient.class, serverIP, Integer.parseInt(serverPort))
                    .onResponse(NetworkEvents.StartClientResponse.class,
                            e -> Platform.runLater(
                                    () -> App.activate(new ConnectedLayer(e.clientId(), player1Name))
                            ))
                    .postEvent();

		});

		player1Container.addToggle(AppContext.getString("human"), AppContext.getString("computer"), !isPlayer1Human, (computer) -> {
			isPlayer1Human = !computer;
			reload();
		});

		if (isPlayer1Human) {
			player1Container.addText(AppContext.getString("playerName"), true);
			player1Container.addInput(player1Name, (name) -> {
				player1Name = name;
			});
		} else {
            player1Name = "PismBot" + LocalDateTime.now().getSecond();
			player1Container.addText(AppContext.getString("computerDifficulty"), true);
			player1Container.addSlider(10, computer1Difficulty, (difficulty) ->
					computer1Difficulty = difficulty);
		}

		if (isConnectionLocal) {
			player2Container.addToggle(AppContext.getString("human"), AppContext.getString("computer"), !isPlayer2Human, (computer) -> {
				isPlayer2Human = !computer;
				reload();
			});

			if (isPlayer2Human) {
				player2Container.addText(AppContext.getString("playerName"), true);
				player2Container.addInput(player2Name, (name) -> {
					player2Name = name;
				});
			} else {
				player2Container.addText(AppContext.getString("computerDifficulty"), true);
				player2Container.addSlider(10, computer2Difficulty, (difficulty) ->
						computer2Difficulty = difficulty);
			}
		} else {
			player2Container.addText(AppContext.getString("serverIP"), true);
			player2Container.addInput(serverIP, (ip) -> {
				serverIP = ip;
			});

			player2Container.addSeparator(true);

			player2Container.addText(AppContext.getString("serverPort"), true);
			player2Container.addInput(serverPort, (port) -> {
				serverPort = port;
			});
		}

		final Container controlContainer = new VerticalContainer(5);

		controlContainer.addButton(AppContext.getString("back"), () -> {
			App.activate(new MainLayer());
		});

		addContainer(mainContainer, Pos.CENTER, 0, 0, 75, 75);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}
}