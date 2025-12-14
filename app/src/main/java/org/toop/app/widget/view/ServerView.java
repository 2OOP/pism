package org.toop.app.widget.view;

import javafx.collections.FXCollections;
import javafx.css.converter.StringConverter;
import javafx.scene.control.ComboBox;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;

import java.util.List;
import java.util.Locale;
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

	private final ComboBox<String> gameList;
	private final ListView<Button> listView;
	private Button subscribeButton;

	public ServerView(String user, Consumer<String> onPlayerClicked, long clientId) {
		this.user = user;
		this.onPlayerClicked = onPlayerClicked;
		this.clientId = clientId;

		this.gameList = new ComboBox<>();
		this.listView = new ListView<>();

		setupLayout();
	}

	private void setupLayout() {
		var playerHeader = Primitive.header(user, false);

		subscribeButton = Primitive.button(
				"subscribe",
				() -> new EventFlow().addPostEvent(new NetworkEvents.SendSubscribe(clientId, gameList.getValue())).postEvent(),
				false,
				true
		); // TODO localize

		var subscribe = Primitive.hbox(gameList, subscribeButton);

		var playerListSection = Primitive.vbox(
			playerHeader,
			Primitive.separator(),
			subscribe,
			listView
		);

		add(Pos.CENTER, playerListSection);

		var disconnectButton = Primitive.button(
				"disconnect", () -> transitionPrevious(), false);

		add(Pos.BOTTOM_LEFT, Primitive.vbox(disconnectButton));
	}

	public void update(List<String> players) {
		Platform.runLater(() -> {
			listView.getItems().clear();

			for (String player : players) {
				var playerButton = Primitive.button(player, () -> onPlayerClicked.accept(player), false, false);
				listView.getItems().add(playerButton);
			}
		});
	}

	public void updateGameList(List<String> games) {
		Platform.runLater(() -> {
			gameList.getItems().clear();
			gameList.setItems(FXCollections.observableArrayList(games));
			gameList.getSelectionModel().select(0);
		});
	}

	public void reEnableButton() {
		subscribeButton.setDisable(false);
	}
}