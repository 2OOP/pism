package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;

import javafx.geometry.Pos;

public final class QuitLayer extends Layer {
	public QuitLayer() {
		super("quit.css", "quit_background");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container mainContainer = new VerticalContainer(30);
		mainContainer.addText("Are you sure?", false);

		final Container controlContainer = new HorizontalContainer(30);
		mainContainer.addContainer(controlContainer, false);

		controlContainer.addButton("Yes", () -> { App.quit(); });
		controlContainer.addButton("No", () -> { App.pop(); });

		addContainer(mainContainer, Pos.CENTER, 0, 0, 30, 30);
	}
}