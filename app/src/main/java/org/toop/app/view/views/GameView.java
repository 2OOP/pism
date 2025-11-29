package org.toop.app.view.views;

import javafx.application.Platform;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.app.view.displays.SongDisplay;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public final class GameView extends View {
    // TODO: This should be it's own file...
	private static class GameOverView extends View {
		private final boolean iWon;
		private final String winner;

        // TODO: Make winner generic, there is no "I won" unless you play online or against bot. Should be a generic "... won" to simplify
		public GameOverView(boolean iWon, String winner) {
			super(false, "bg-popup");

			this.iWon = iWon;
			this.winner = winner;
		}

		@Override
		public void setup() {
			final Text gameOverHeader = header();
			gameOverHeader.setText(AppContext.getString("game-over"));

			final Button okButton = button();
			okButton.setText(AppContext.getString("ok"));
			okButton.setOnAction(_ -> { ViewStack.pop(); });

			Text gameOverText = text();

			if (winner.isEmpty()) {
				gameOverText.setText(AppContext.getString("the-game-ended-in-a-draw"));
			} else {
				if (iWon) {
					gameOverText.setText(AppContext.getString("you-win") + " " + winner);
				} else {
					gameOverText.setText(AppContext.getString("you-lost-against") + " " + winner);
				}
			}

			add(Pos.CENTER,
				fit(vboxFill(
					gameOverHeader,
					separator(),

					vspacer(),
					gameOverText,
					vspacer(),

					separator(),
					okButton
				))
			);
		}
	}

	private final Button forfeitButton;
	private final Button exitButton;

	private final Text currentPlayerHeader;
	private final Text currentMoveHeader;

	private final Text nextPlayerHeader;

    private final Text gameStateFeedback = text();

	private final ListView<Text> chatListView;
	private final TextField chatInput;

	public GameView(Runnable onForfeit, Runnable onExit, Consumer<String> onMessage) {
		assert onExit != null;

		super(true, "bg-primary");

		if (onForfeit != null) {
			forfeitButton = button();
			forfeitButton.setText(AppContext.getString("forfeit"));
			forfeitButton.setOnAction(_ -> onForfeit.run());
		} else {
			forfeitButton = null;
		}

        final SongDisplay songdisplay = new SongDisplay();


        add(Pos.BOTTOM_RIGHT,
                fit(vboxFill(
                        songdisplay
                )));

		if (onMessage != null) {
			chatListView = new ListView<Text>();

			chatInput = input();
			chatInput.setOnAction(_ -> {
				onMessage.accept(chatInput.getText());
				chatInput.setText("");
			});
		} else {
			chatListView = null;
			chatInput = null;
		}

		exitButton = button();
		exitButton.setText(AppContext.getString("exit"));
		exitButton.setOnAction(_ -> onExit.run());

		currentPlayerHeader = header("", "header");
		currentMoveHeader = header();
		nextPlayerHeader = header();
	}

	@Override
	public void setup() {
		add(
                Pos.TOP_CENTER,
                gameStateFeedback
		);

		add(Pos.BOTTOM_LEFT,
                vboxFill(
                    forfeitButton,
                    exitButton
                )
        );

		if (chatListView != null) {
			add(Pos.BOTTOM_RIGHT,
				fit(vboxFill(
					chatListView,
					chatInput
				)
			));
		}
	}

	public void nextPlayer(boolean isMe, String currentPlayer, String currentMove, String nextPlayer) {
        Platform.runLater(() -> {
            gameStateFeedback.setText("Waiting on " + currentPlayer + " to make their move.");
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

	public void updateChat(String message) {
		if (chatListView == null) {
			return;
		}

		final Text messageText = text();
		messageText.setText(message);

		chatListView.getItems().add(messageText);
	}

	public void gameOver(boolean iWon, String winner) {
		ViewStack.push(new GameOverView(iWon, winner));
	}
}