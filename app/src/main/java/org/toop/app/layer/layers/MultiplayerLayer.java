package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.GameType;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;

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

		final Container connectionContainer = Container.create(Container.Type.VERTICAL, 0);
		connectionContainer.addToggle("Local", "Server", !isConnectionLocal, (server) -> {
			if (server) {
				System.out.println("Server is checked now");
			}

			isConnectionLocal = !server;
			reload();
		});

		final Container player1Container = Container.create(Container.Type.VERTICAL, 5);

		player1Container.addToggle("Human", "Computer", !isPlayer1Human, (computer) -> {
			isPlayer1Human = !computer;
			reload();
		});

		if (isPlayer1Human) {
			player1Container.addText("player is human");
			player1Container.addText("input player name here: ...");
		} else {
			player1Container.addText("playing against ai");
			player1Container.addToggle("Easy", "Hard", false, (hard) -> {});
		}

		final Container player2Container = Container.create(Container.Type.VERTICAL, 5);

		if (isConnectionLocal) {
			player2Container.addToggle("Human", "Computer", !isPlayer2Human, (computer) -> {
				isPlayer2Human = !computer;
				reload();
			});

			if (isPlayer2Human) {
				player2Container.addText("player is human");
				player2Container.addText("input player name here: ...");
			} else {
				player2Container.addText("playing against ai");
				player2Container.addToggle("Easy", "Hard", false, (hard) -> {});
			}
		} else {
			player2Container.addText("enter server ip here: ...");
			player2Container.addText("enter server port here: ...");
		}

		final Container controlContainer = Container.create(Container.Type.VERTICAL, 5);
		controlContainer.addButton("Back", () -> { App.activate(new MainLayer()); });

		addContainer(connectionContainer, Pos.TOP_CENTER, 0, 20);

		addContainer(player1Container, Pos.CENTER_LEFT, 10, 0);
		addContainer(player2Container, Pos.CENTER_RIGHT, -10, 0);

		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2);
	}
}