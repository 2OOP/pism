package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;

import javafx.geometry.Pos;

public final class QuitLayer extends Layer {
	public QuitLayer() {
		super("quit.css");

		final Container mainContainer = Container.create(Container.Type.VERTICAL, 30);
		mainContainer.addText("Are you sure?");

		final Container controlContainer = mainContainer.addContainer(Container.Type.HORIZONTAL, 50);
		controlContainer.addButton("Yes", () -> { App.quit(); });
		controlContainer.addButton("No", () -> { App.pop(); });

		addContainer(mainContainer, Pos.CENTER, 0, 0);
	}
}