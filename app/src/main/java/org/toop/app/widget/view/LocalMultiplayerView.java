package org.toop.app.widget.view;

import javafx.application.Platform;
import org.toop.app.GameInformation;
import org.toop.app.game.Connect4Game;
import org.toop.app.game.ReversiGame;
import org.toop.app.game.TicTacToeGameThread;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.PlayerInfoWidget;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.app.widget.popup.ErrorPopup;
import org.toop.app.widget.tutorial.BaseTutorialWidget;
import org.toop.app.widget.tutorial.TicTacToeTutorialWidget;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.toop.local.AppSettings;
public class LocalMultiplayerView extends ViewWidget {
	private final GameInformation information;

	public LocalMultiplayerView(GameInformation.Type type) {
		this(new GameInformation(type));
	}

	public LocalMultiplayerView(GameInformation information) {
		this.information = information;
		var playButton = Primitive.button("play", () -> {
			for (var player : information.players) {
				if (player.isHuman && player.name.isEmpty()) {
					new ErrorPopup(AppContext.getString("please-enter-your-name")).show(Pos.CENTER);
					return;
				}
			}

			switch (information.type) {
                case TICTACTOE:
                    System.out.println(AppSettings.getSettings().getTutorialFlag());
                    System.out.println(AppSettings.getSettings().getFirstTTT());
                    if (AppSettings.getSettings().getTutorialFlag() && AppSettings.getSettings().getFirstTTT()) {
                         BaseTutorialWidget a = new BaseTutorialWidget(
                                "tutorial",
                                () -> {
                                    AppSettings.getSettings().setFirstTTT(false);
                                    Platform.runLater(() -> {
                                        new TicTacToeGameThread(information);
                                    });
                                },
                                () -> {
                                    Platform.runLater(() -> {
                                        ViewWidget c = new TicTacToeTutorialWidget();
                                        transitionNext(c);
                                        WidgetContainer.setCurrentView(c);
                                    });
                                    },
                                () -> {
                                    AppSettings.getSettings().setTutorialFlag(false);
                                    Platform.runLater(() -> {
                                        new TicTacToeGameThread(information);
                                    });
                                }
                        );
                         transitionNext(a);
                         break;
                    }
                    new TicTacToeGameThread(information);
                    break;
                case REVERSI: new ReversiGame(information);
                case CONNECT4: new Connect4Game(information);
				// case BATTLESHIP -> new BattleshipGame(information);
			}
		});

		var playerSection = setupPlayerSections();

		add(Pos.CENTER, Primitive.vbox(
			playerSection,
			Primitive.separator(),
			playButton
		));
	}

	private ScrollPane setupPlayerSections() {
		int playerCount = information.type.getPlayerCount();
		VBox[] playerBoxes = new VBox[playerCount];

		for (int i = 0; i < playerCount; i++) {
			var player = information.players[i];

			var playerHeader = Primitive.header("");
			playerHeader.setText("player" + " #" + (i + 1));

			var playerWidget = new PlayerInfoWidget(player);

			playerBoxes[i] = Primitive.vbox(
				playerHeader,
				Primitive.separator(),
				playerWidget.getNode()
			);
		}

		return Primitive.scroll(Primitive.hbox(
			playerBoxes
		));
	}
}