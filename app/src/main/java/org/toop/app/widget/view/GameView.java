package org.toop.app.widget.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.app.widget.popup.GameOverPopup;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.toop.app.widget.tutorial.Connect4TutorialWidget;
import org.toop.app.widget.tutorial.ReversiTutorialWidget;
import org.toop.app.widget.tutorial.TicTacToeTutorialWidget;
import org.toop.local.AppContext;

public final class GameView extends ViewWidget {
	private final Text playerHeader;
	private final Text turnHeader;
    private final Text player1Header;
    private final Text player2Header;
    private final Circle player1Icon;
    private final Circle player2Icon;
	private final Button forfeitButton;
	private final Button exitButton;
    private final TextField chatInput;
    private final Text keyThingy;
    private boolean hasSet = false;

	public GameView(Runnable onForfeit, Runnable onExit, Consumer<String> onMessage, String gameType) {
		playerHeader = Primitive.header("");
		turnHeader = Primitive.header("");
        keyThingy = Primitive.text("turnof");
        player1Header = Primitive.header("");
        player2Header = Primitive.header("");
        player1Icon = new Circle();
        player2Icon = new Circle();

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

        switch (gameType) {
            case "TicTacToe":
                AppContext.setCurrentTutorial(new TicTacToeTutorialWidget(() -> {}));
                break;
            case "Reversi":
                AppContext.setCurrentTutorial(new ReversiTutorialWidget(() -> {}));
                break;
            case "Connect4":
                AppContext.setCurrentTutorial(new Connect4TutorialWidget(() -> {}));
                break;
        }

		setupLayout();
	}

	private void setupLayout() {
        var turnInfo = Primitive.vbox(
                turnHeader
        );

        add(Pos.TOP_CENTER, turnInfo);

		var buttons = Primitive.vbox(
			forfeitButton,
			exitButton
		);

		add(Pos.BOTTOM_LEFT, buttons);

		if (chatInput != null) {
			add(Pos.BOTTOM_RIGHT, Primitive.vbox(chatInput));
		}
	}

	public void updatePlayerInfo(boolean isMe, String currentPlayer, String currentMove, String nextPlayer, String GameType) {
		Platform.runLater(() -> {
            if (!(hasSet)) {
                playerHeader.setText(currentPlayer + " vs. " + nextPlayer);
                hasSet = true;
                setPlayerHeaders(isMe, currentPlayer, nextPlayer, GameType);
            }
            //TODO idk if theres any way to check this? only EN uses 's and the rest doesnt. if theres a better way to do this pls let me know
            if (AppContext.getLocale().toLanguageTag().equals("en")) {
                turnHeader.setText(currentPlayer + keyThingy.getText());
            }
		});
	}

	public void gameOver(boolean iWon, String winner) {
		new GameOverPopup(iWon, winner).show(Pos.CENTER);
	}

    private void setPlayerHeaders(boolean isMe, String currentPlayer, String nextPlayer, String GameType) {
        if (GameType.equals("TicTacToe")) {
            if (isMe) {
                player1Header.setText("X: " + currentPlayer);
                player2Header.setText("O: " + nextPlayer);
            }
            else {
                player1Header.setText("X: " + nextPlayer);
                player2Header.setText("O: " + currentPlayer);
            }
            setPlayerInfoTTT();
        }
        else if (GameType.equals("Reversi")) {
            if (isMe) {
                player1Header.setText(nextPlayer);
                player2Header.setText(currentPlayer);
            }
            else {
                player1Header.setText(currentPlayer);
                player2Header.setText(nextPlayer);
            }
            setPlayerInfoReversi();
        }
    }

    private void setPlayerInfoTTT() {
        var playerInfo = Primitive.vbox(
                playerHeader,
                Primitive.separator(),
                player1Header,
                player2Header
        );

        add(Pos.TOP_RIGHT, playerInfo);
    }

    private void setPlayerInfoReversi() {
        var player1box = Primitive.hbox(
                player1Icon,
                player1Header
        );

        player1box.getStyleClass().add("hboxspacing");

        var player2box = Primitive.hbox(
                player2Icon,
                player2Header
        );

        player2box.getStyleClass().add("hboxspacing");

        var playerInfo = Primitive.vbox(
                playerHeader,
                Primitive.separator(),
                player2box,
                player1box
        );

        player1Icon.setRadius(player1Header.fontProperty().map(Font::getSize).getValue());
        player2Icon.setRadius(player2Header.fontProperty().map(Font::getSize).getValue());
        player1Icon.setFill(Color.BLACK);
        player2Icon.setFill(Color.WHITE);
        add(Pos.TOP_RIGHT, playerInfo);
    }
}