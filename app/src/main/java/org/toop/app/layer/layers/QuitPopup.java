package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.Popup;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.local.AppContext;

import javafx.geometry.Pos;

public final class QuitPopup extends Popup {
	public QuitPopup() {
		super(true);
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final var sureText = NodeBuilder.header(AppContext.getString("quitSure"));

		final var yesButton = NodeBuilder.button(AppContext.getString("yes"), () -> {
			App.quit();
		});

		final var noButton = NodeBuilder.button(AppContext.getString("no"), () -> {
			App.pop();
		});

		final Container controlContainer = new HorizontalContainer(30);
		controlContainer.addNodes(yesButton, noButton);

		final Container mainContainer = new VerticalContainer(30);
		mainContainer.addNodes(sureText);
		mainContainer.addContainer(controlContainer, false);

		addContainer(mainContainer, Pos.CENTER, 0, 0, 30, 30);
	}
}