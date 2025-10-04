package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.game.TurnBasedGame;

import javafx.geometry.Pos;

public final class MultiplayerLayer<T extends TurnBasedGame> extends Layer {
	private boolean isConnectionLocal = true;

	private boolean isPlayer1Human = true;
	private String player1Name = "";
	private int computer1Difficulty = 0;

	private boolean isPlayer2Human = true;
	private String player2Name = "";
	private int computer2Difficulty = 0;

	private String serverIP = "";
	private String serverPort = "";

	public MultiplayerLayer() {
		super("multiplayer.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container mainContainer = new VerticalContainer(5);

		mainContainer.addToggle("Local", "Server", !isConnectionLocal, (server) -> {
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

		if (isConnectionLocal) {
			mainContainer.addButton("Start", () -> {
			});
		} else {
			mainContainer.addButton("Connect", () -> {
			});
		}

		player1Container.addToggle("Human", "Computer", !isPlayer1Human, (computer) -> {
			isPlayer1Human = !computer;
			reload();
		});

		if (isPlayer1Human) {
			player1Container.addText("Player name", true);
			player1Container.addInput(player1Name, (name) -> {
				player1Name = name;
			});
		} else {
			player1Container.addText("Computer difficulty", true);
			player1Container.addSlider(5, computer1Difficulty, (difficulty) -> {
				computer1Difficulty = difficulty;
			});
		}

		if (isConnectionLocal) {
			player2Container.addToggle("Human", "Computer", !isPlayer2Human, (computer) -> {
				isPlayer2Human = !computer;
				reload();
			});

			if (isPlayer2Human) {
				player2Container.addText("Player name", true);
				player2Container.addInput(player2Name, (name) -> {
					player2Name = name;
				});
			} else {
				player2Container.addText("Computer difficulty", true);
				player2Container.addSlider(5, computer2Difficulty, (difficulty) -> {
					computer2Difficulty = difficulty;
				});
			}
		} else {
			player2Container.addText("Server IP", true);
			player2Container.addInput(serverIP, (ip) -> {
				serverIP = ip;
			});

			player2Container.addSeparator(true);

			player2Container.addText("Server Port", true);
			player2Container.addInput(serverPort, (port) -> {
				serverPort = port;
			});
		}

		final Container controlContainer = new VerticalContainer(5);

		controlContainer.addButton("Back", () -> {
			App.activate(new MainLayer());
		});

		addContainer(mainContainer, Pos.CENTER, 0, 0, 75, 75);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}
}