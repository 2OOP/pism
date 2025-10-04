package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.GameType;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;

import javafx.geometry.Pos;

public final class MainLayer extends Layer {
	public MainLayer() {
		super("main.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container gamesContainer = new VerticalContainer(5);

		gamesContainer.addButton("Tic Tac Toe", () -> {
			App.activate(new MultiplayerLayer(GameType.TICTACTOE));
		});

		gamesContainer.addButton("Othello", () -> {
			App.activate(new MultiplayerLayer(GameType.OTHELLO));
		});

		final Container controlContainer = new VerticalContainer(5);

		controlContainer.addButton("Credits", () -> {
		});

		controlContainer.addButton("Options", () -> {
		});

		controlContainer.addButton("Quit", () -> {
			App.quitPopup();
		});

		addContainer(gamesContainer, Pos.TOP_LEFT, 2, 2, 25, 0);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 25, 0);
	}
}