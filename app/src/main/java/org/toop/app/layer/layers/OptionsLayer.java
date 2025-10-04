package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;

import javafx.geometry.Pos;

public final class OptionsLayer extends Layer {
	OptionsLayer() {
		super("options.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container controlContainer = new VerticalContainer(5);

		controlContainer.addButton("Back", () -> {
			App.activate(new MainLayer());
		});

		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}
}