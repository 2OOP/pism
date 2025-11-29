package org.toop.app.widget.view;

import javafx.application.Platform;
import org.toop.app.GameInformation;
import org.toop.app.game.*;
import org.toop.app.game.gameManagers.TicTacToeManager;
import org.toop.game.players.ArtificialPlayer;
import org.toop.game.players.LocalPlayer;
import org.toop.game.players.AbstractPlayer;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.PlayerInfoWidget;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.app.widget.popup.ErrorPopup;
import org.toop.app.widget.tutorial.BaseTutorialWidget;
import org.toop.app.widget.tutorial.Connect4TutorialWidget;
import org.toop.app.widget.tutorial.ReversiTutorialWidget;
import org.toop.app.widget.tutorial.TicTacToeTutorialWidget;
import org.toop.game.tictactoe.TicTacToeAIR;
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

            // TODO: Fix this temporary ass way of setting the players (Only works for TicTacToe)
            AbstractPlayer[] players = new AbstractPlayer[2];
            if (information.players[0].isHuman){
                players[0] = new LocalPlayer(information.players[0].name);
            }
            else {
                players[0] = new ArtificialPlayer<>(new TicTacToeAIR(), information.players[0].name);
            }
            if (information.players[1].isHuman){
                players[1] = new LocalPlayer(information.players[1].name);
            }
            else {
                players[1] = new ArtificialPlayer<>(new TicTacToeAIR(), information.players[1].name);
            }

			switch (information.type) {
                case TICTACTOE:
                    if (AppSettings.getSettings().getTutorialFlag() && AppSettings.getSettings().getFirstTTT()) {
                         BaseTutorialWidget a = new BaseTutorialWidget(
                                "tutorial",
                                () -> {
                                    AppSettings.getSettings().setFirstTTT(false);
                                    Platform.runLater(() -> {
                                        new TicTacToeManager(players);
                                    });
                                },
                                () -> {
                                        ViewWidget c = new TicTacToeTutorialWidget();
                                        transitionNext(c);
                                        WidgetContainer.setCurrentView(c);
                                        AppSettings.getSettings().setFirstTTT(false);
                                    },
                                () -> {
                                    AppSettings.getSettings().setTutorialFlag(false);
                                    Platform.runLater(() -> {
                                        new TicTacToeManager(players);
                                    });
                                }
                        );
                         transitionNext(a);
                         break;
                    }
                    new TicTacToeManager(players);
                    break;
                case REVERSI:
                    if (AppSettings.getSettings().getTutorialFlag() && AppSettings.getSettings().getFirstReversi()) {
                        BaseTutorialWidget a = new BaseTutorialWidget(
                                "tutorial",
                                () -> { Platform.runLater(() -> {
                                    AppSettings.getSettings().setFirstReversi(false);
                                    new ReversiGame(information);
                                });
                                },
                                () -> {
                                    Platform.runLater(() -> {
                                        ViewWidget c = new ReversiTutorialWidget();
                                        transitionNext(c);
                                        WidgetContainer.setCurrentView(c);
                                        AppSettings.getSettings().setFirstReversi(false);
                                    });
                                },
                                () -> {
                                    Platform.runLater(() -> {
                                        AppSettings.getSettings().setTutorialFlag(false);
                                        new ReversiGame(information);
                                    });
                                });
                        transitionNext(a);
                        break;
                    }
                    new ReversiGame(information);
                    break;
                case CONNECT4:
                    if (AppSettings.getSettings().getTutorialFlag() && AppSettings.getSettings().getFirstConnect4()) {
                        BaseTutorialWidget a = new BaseTutorialWidget(
                                "tutorial",
                                () -> { Platform.runLater(() -> {
                                    AppSettings.getSettings().setFirstConnect4(false);
                                    new Connect4Game(information);
                                });
                                },
                                () -> {
                                    Platform.runLater(() -> {
                                        ViewWidget c = new Connect4TutorialWidget();
                                        transitionNext(c);
                                        WidgetContainer.setCurrentView(c);
                                        AppSettings.getSettings().setFirstConnect4(false);
                                    });
                                },
                                () -> {
                                    Platform.runLater(() -> {
                                        AppSettings.getSettings().setTutorialFlag(false);
                                        new Connect4Game(information);
                                    });
                                    });
                        transitionNext(a);
                        break;
                    }
                    new Connect4Game(information);
                    break;
                    }
				// case BATTLESHIP -> new BattleshipGame(information);
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