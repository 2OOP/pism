package org.toop.app.widget.view;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.app.widget.popup.GameOverPopup;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public final class GameView extends ViewWidget {
	private final Text currentPlayerHeader;
	private final Text currentMoveHeader;
	private final Text nextPlayerHeader;

	private final Button forfeitButton;
	private final Button exitButton;

	private final TextField chatInput;

	public GameView(Runnable onForfeit, Runnable onExit, Consumer<String> onMessage) {
		currentPlayerHeader = Primitive.header("");
		currentMoveHeader = Primitive.header("");
		nextPlayerHeader = Primitive.header("");

		if (onForfeit != null) {
			forfeitButton = Primitive.button("forfeit", () -> onForfeit.run());
		} else {
			forfeitButton = null;
		}

		exitButton = Primitive.button("exit", () -> {
			onExit.run();
			transitionPrevious();
		});

		if (onMessage != null) {
			chatInput = Primitive.input("enter-your-message", "", null);
			chatInput.setOnAction(_ -> {
				onMessage.accept(chatInput.getText());
				chatInput.clear();
			});
		} else {
			chatInput = null;
		}

		setupLayout();
	}

	private void setupLayout() {
		var playerInfo = Primitive.vbox(
			currentPlayerHeader,
			Primitive.hbox(
				Primitive.separator(),
				currentMoveHeader,
				Primitive.separator()
			),
			nextPlayerHeader
		);

		add(Pos.TOP_RIGHT, playerInfo);

		var buttons = Primitive.vbox(
			forfeitButton,
			exitButton
		);

		add(Pos.BOTTOM_LEFT, buttons);

		if (chatInput != null) {
			add(Pos.BOTTOM_RIGHT, Primitive.vbox(chatInput));
		}
	}

	public void nextPlayer(boolean isMe, String currentPlayer, String currentMove, String nextPlayer) {
		Platform.runLater(() -> {
			currentPlayerHeader.setText(currentPlayer);
			currentMoveHeader.setText(currentMove);
			nextPlayerHeader.setText(nextPlayer);

			if (isMe) {
				currentPlayerHeader.getStyleClass().add("my-turn");
			} else {
				currentPlayerHeader.getStyleClass().remove("my-turn");
			}
		});
	}

	public void gameOver(boolean iWon, String winner) {
		new GameOverPopup(iWon, winner).show(Pos.CENTER);
	}
}