package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.GameType;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;

import javafx.geometry.Pos;

public final class MainLayer extends Layer {
	public MainLayer() {
		super("main.css");

		final Container gamesContainer = Container.create(Container.Type.VERTICAL, 5);
		gamesContainer.addButton("Tic Tac Toe", () -> { App.activate(new MultiplayerLayer(GameType.TICTACTOE)); });
		gamesContainer.addButton("Othello", () -> { App.activate(new MultiplayerLayer(GameType.OTHELLO)); });

		final Container controlContainer = Container.create(Container.Type.VERTICAL, 5);
		controlContainer.addButton("Credits", () -> {});
		controlContainer.addButton("Options", () -> {});
		controlContainer.addButton("Quit", () -> { App.quitPopup(); });

		addContainer(gamesContainer, Pos.TOP_LEFT, 2, 2);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2);
	}

	@Override
	public void reload() {
	}
}