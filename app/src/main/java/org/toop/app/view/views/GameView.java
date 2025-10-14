package org.toop.app.view.views;

import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public final class GameView extends View {
	private static class GameOverView extends View {
		private final boolean iWon;
		private final String winner;

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

	public GameView(Runnable onForfeit, Runnable onExit) {
		assert onExit != null;

		super(true, "bg-primary");

		if (onForfeit != null) {
			forfeitButton = button();
			forfeitButton.setText(AppContext.getString("forfeit"));
			forfeitButton.setOnAction(_ -> onForfeit.run());
		} else {
			forfeitButton = null;
		}

		exitButton = button();
		exitButton.setText(AppContext.getString("exit"));
		exitButton.setOnAction(_ -> onExit.run());

		currentPlayerHeader = header("", "current-player");
		currentMoveHeader = header();

		nextPlayerHeader = header();
	}

	@Override
	public void setup() {
		add(Pos.TOP_RIGHT,
			fit(vboxFill(
				currentPlayerHeader,

				hboxFill(
					separator(),
					currentMoveHeader,
					separator()
				),

				nextPlayerHeader
			))
		);

		add(Pos.BOTTOM_LEFT,
			vboxFill(
				forfeitButton,
				exitButton
			)
		);
	}

	public void nextPlayer(boolean isMe, String currentPlayer, String currentMove, String nextPlayer) {
		currentPlayerHeader.setText(currentPlayer);
		currentMoveHeader.setText(currentMove);

		nextPlayerHeader.setText(nextPlayer);

		if (isMe) {
			currentPlayerHeader.getStyleClass().add("my-turn");
		} else {
			currentPlayerHeader.getStyleClass().remove("my-turn");
		}
	}

	public void updateChat(String player, String message) {
		// Todo
	}

	public void gameOver(boolean iWon, String winner) {
		ViewStack.push(new GameOverView(iWon, winner));
	}
}