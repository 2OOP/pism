package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.GameType;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;

import javafx.geometry.Pos;

public class MultiplayerLayer extends Layer {
	boolean isConnectionLocal = true;

	boolean isPlayer1Human = true;
	boolean isPlayer2Human = true;

	protected MultiplayerLayer(GameType type) {
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

		player1Container.addToggle("Human", "Computer", !isPlayer1Human, (computer) -> {
			isPlayer1Human = !computer;
			reload();
		});

		if (isPlayer1Human) {
			player1Container.addText("player is human", true);
			player1Container.addText("input player name here: ...", true);
		} else {
			player1Container.addText("playing against ai", true);
			player1Container.addToggle("Easy", "Hard", false, (hard) -> {});
		}

		if (isConnectionLocal) {
			player2Container.addToggle("Human", "Computer", !isPlayer2Human, (computer) -> {
				isPlayer2Human = !computer;
				reload();
			});

			if (isPlayer2Human) {
				player2Container.addText("player is human", true);
				player2Container.addText("input player name here: ...", true);
			} else {
				player2Container.addText("playing against ai", true);
				player2Container.addToggle("Easy", "Hard", false, (hard) -> {});
			}
		} else {
			player2Container.addText("enter server ip here: ...", true);
			player2Container.addText("enter server port here: ...", true);
		}

		final Container controlContainer = new VerticalContainer(5);
		controlContainer.addButton("Back", () -> { App.activate(new MainLayer()); });

		addContainer(mainContainer, Pos.CENTER, 0, 0, 75, 75);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}
}