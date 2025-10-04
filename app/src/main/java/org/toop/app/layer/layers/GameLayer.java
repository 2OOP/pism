package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;

import javafx.geometry.Pos;

public class GameLayer extends Layer {
	public GameLayer() {
		super("game.css");
		reload();
	}

	@Override
	public void reload() {
		App.popAll();

		final Container controlContainer = new VerticalContainer(5);

		controlContainer.addButton("Back", () -> {
			App.activate(new MainLayer());
		});

		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}
}