package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;

import javafx.geometry.Pos;

public final class OptionsLayer extends Layer {
	private static boolean isWindowed = true;

	OptionsLayer() {
		super("options.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container mainContainer = new VerticalContainer(50);

		mainContainer.addText("Options", false);

		final Container optionsContainer = new VerticalContainer(5);

		mainContainer.addContainer(optionsContainer, true);

		optionsContainer.addToggle("Windowed", "Fullscreen", !isWindowed, (fullscreen) -> {
			isWindowed = !fullscreen;
			App.setFullscreen(fullscreen);
		});

		final Container controlContainer = new VerticalContainer(5);

		controlContainer.addButton("Back", () -> {
			App.activate(new MainLayer());
		});

		addContainer(mainContainer, Pos.CENTER, 0, 0, 30, 60);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}
}