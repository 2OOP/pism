package org.toop.app.widget.view;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;

import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.connection.events.NetworkEvents;

public final class ServerView extends ViewWidget {
	private final String user;
	private final Consumer<String> onPlayerClicked;
	private final long clientId;

	private final ListView<Button> listView;

	public ServerView(String user, Consumer<String> onPlayerClicked, long clientId) {
		this.user = user;
		this.onPlayerClicked = onPlayerClicked;
		this.clientId = clientId;

		this.listView = new ListView<>();

		setupLayout();
	}

	private void setupLayout() {
		var playerHeader = Primitive.header(user, false);

		Button subscribeButton = Primitive.button(
				"subscribe",
				() -> new EventFlow().addPostEvent(new NetworkEvents.SendSubscribe(clientId, "reversi")).postEvent(),
				false
		); // TODO localize

		var playerListSection = Primitive.vbox(
			playerHeader,
			Primitive.separator(),
			subscribeButton,
			listView
		);

		add(Pos.CENTER, playerListSection);

		var disconnectButton = Primitive.button("disconnect", () -> {
			transitionPrevious();
		});

		add(Pos.BOTTOM_LEFT, Primitive.vbox(disconnectButton));
	}

	public void update(List<String> players) {
		Platform.runLater(() -> {
			listView.getItems().clear();

			for (String player : players) {
				var playerButton = Primitive.button(player, () -> onPlayerClicked.accept(player), false);
				listView.getItems().add(playerButton);
			}
		});
	}
}