package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.local.AppContext;

import javafx.geometry.Pos;

public final class QuitLayer extends Layer {
	public QuitLayer() {
		super("quit.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container mainContainer = new VerticalContainer(30);
		mainContainer.addText(AppContext.getString("quitSure"), false);

		final Container controlContainer = new HorizontalContainer(30);

		mainContainer.addContainer(controlContainer, false);

		controlContainer.addButton(AppContext.getString("yes"), () -> {
			App.quit();
		});

		controlContainer.addButton(AppContext.getString("no"), () -> {
			App.pop();
		});

		addContainer(mainContainer, Pos.CENTER, 0, 0, 30, 30);
	}
}