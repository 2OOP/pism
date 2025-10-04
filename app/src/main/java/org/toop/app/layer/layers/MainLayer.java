package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.game.othello.Othello;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.local.AppContext;

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

		gamesContainer.addButton(AppContext.getString("tictactoe"), () -> {
			App.activate(new MultiplayerLayer<TicTacToe>());
		});

		gamesContainer.addButton(AppContext.getString("othello"), () -> {
			App.activate(new MultiplayerLayer<Othello>());
		});

		final Container controlContainer = new VerticalContainer(5);

		controlContainer.addButton(AppContext.getString("credits"), () -> {
			App.activate(new CreditsLayer());
		});

		controlContainer.addButton(AppContext.getString("options"), () -> {
			App.activate(new OptionsLayer());
		});

		controlContainer.addButton(AppContext.getString("quit"), () -> {
			App.quitPopup();
		});

		addContainer(gamesContainer, Pos.TOP_LEFT, 2, 2, 20, 0);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 20, 0);
	}
}