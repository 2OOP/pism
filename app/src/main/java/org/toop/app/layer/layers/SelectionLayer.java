package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.GameType;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;

import javafx.geometry.Pos;

public class SelectionLayer extends Layer {
	protected SelectionLayer(GameType type) {
		super("selection.css");

		final Container controlContainer = Container.create(Container.Type.VERTICAL, 5);
		controlContainer.addButton("Back", () -> { App.activate(new MainLayer()); });

		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2);
	}
}