package org.toop.app.widget.view;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;

import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public final class ServerView extends ViewWidget {
	private final String user;
	private final Consumer<String> onPlayerClicked;
	private final Runnable onDisconnect;

	private final ListView<Button> listView;

	public ServerView(String user, Consumer<String> onPlayerClicked, Runnable onDisconnect) {
		this.user = user;
		this.onPlayerClicked = onPlayerClicked;
		this.onDisconnect = onDisconnect;

		this.listView = new ListView<>();

		setupLayout();
	}

	private void setupLayout() {
		var playerHeader = Primitive.header(user);

		var playerListSection = Primitive.vbox(
			playerHeader,
			Primitive.separator(),
			listView
		);

		add(Pos.CENTER, playerListSection);

		var disconnectButton = Primitive.button("disconnect", () -> {
			onDisconnect.run();
			transitionPrevious();
		});

		add(Pos.BOTTOM_LEFT, Primitive.vbox(disconnectButton));
	}

	public void update(List<String> players) {
		Platform.runLater(() -> {
			listView.getItems().clear();

			for (String player : players) {
				var playerButton = Primitive.button(player, () -> onPlayerClicked.accept(player));
				listView.getItems().add(playerButton);
			}
		});
	}
}