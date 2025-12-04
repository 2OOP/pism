package org.toop.app.widget.view;

import javafx.application.Platform;
import org.toop.app.GameInformation;
import org.toop.app.canvas.ReversiBitCanvas;
import org.toop.app.canvas.TicTacToeBitCanvas;
import org.toop.app.gameControllers.GenericGameController;
import org.toop.framework.gameFramework.controller.GameController;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.gameThreads.LocalThreadBehaviour;
import org.toop.game.games.reversi.BitboardReversi;
import org.toop.game.games.tictactoe.BitboardTicTacToe;
import org.toop.game.players.ArtificialPlayer;
import org.toop.game.players.LocalPlayer;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.PlayerInfoWidget;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.app.widget.popup.ErrorPopup;
import org.toop.game.games.reversi.ReversiAIR;
import org.toop.game.games.tictactoe.TicTacToeAIR;
import org.toop.app.widget.tutorial.*;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.toop.local.AppSettings;

import java.util.Arrays;

public class LocalMultiplayerView extends ViewWidget {
	private final GameInformation information;

    private GameController gameController;

	public LocalMultiplayerView(GameInformation.Type type) {
		this(new GameInformation(type));
	}

	public LocalMultiplayerView(GameInformation information) {
		this.information = information;
		var playButton = Primitive.button("play", () -> {
            if (gameController != null) {
                gameController.stop();
            }
			for (var player : information.players) {
				if (player.isHuman && player.name.isEmpty()) {
					new ErrorPopup(AppContext.getString("please-enter-your-name")).show(Pos.CENTER);
					return;
				}
			}

            // TODO: Fix this temporary ass way of setting the players (Only works for TicTacToe)
            Player[] players = new Player[2];

			switch (information.type) {
                case TICTACTOE:
                    System.out.println("TicTacToe");
                    if (information.players[0].isHuman) {
                        players[0] = new LocalPlayer<>(information.players[0].name);
                    } else {
                        players[0] = new ArtificialPlayer<>(new TicTacToeAIR(), information.players[0].name);
                    }
                    if (information.players[1].isHuman) {
                        players[1] = new LocalPlayer<>(information.players[1].name);
                    } else {
                        players[1] = new ArtificialPlayer<>(new TicTacToeAIR(), information.players[1].name);
                    }
                    System.out.println(Arrays.toString(players));
                    if (AppSettings.getSettings().getTutorialFlag() && AppSettings.getSettings().getFirstTTT()) {
                        new ShowEnableTutorialWidget(
                                () -> new TicTacToeTutorialWidget(() -> {
                                    BitboardTicTacToe game = new BitboardTicTacToe(players); // TODO: ThreadBehaviour might need to be created by game idk
                                    gameController = new GenericGameController<BitboardTicTacToe>(new TicTacToeBitCanvas(), game, new LocalThreadBehaviour<BitboardTicTacToe>(game), "TicTacToe");
                                    gameController.start();
                                }),
                                () -> Platform.runLater(() -> {
                                    BitboardTicTacToe game = new BitboardTicTacToe(players); // TODO: ThreadBehaviour might need to be created by game idk
                                    gameController = new GenericGameController<BitboardTicTacToe>(new TicTacToeBitCanvas(), game, new LocalThreadBehaviour<BitboardTicTacToe>(game), "TicTacToe");
                                    gameController.start();
                                }),
                                () -> AppSettings.getSettings().setFirstTTT(false)
                        );
                    } else {
                        BitboardTicTacToe game = new BitboardTicTacToe(players); // TODO: ThreadBehaviour might need to be created by game idk
                        gameController = new GenericGameController<BitboardTicTacToe>(new TicTacToeBitCanvas(), game, new LocalThreadBehaviour<BitboardTicTacToe>(game), "TicTacToe");
                        gameController.start();
                    }
                    break;
                case REVERSI:
                    if (information.players[0].isHuman) {
                        players[0] = new LocalPlayer<>(information.players[0].name);
                    } else {
                        players[0] = new ArtificialPlayer<>(new ReversiAIR(), information.players[0].name);
                    }
                    if (information.players[1].isHuman) {
                        players[1] = new LocalPlayer<>(information.players[1].name);
                    } else {
                        players[1] = new ArtificialPlayer<>(new ReversiAIR(), information.players[1].name);
                    }
                    if (AppSettings.getSettings().getTutorialFlag() && AppSettings.getSettings().getFirstReversi()) {
                        new ShowEnableTutorialWidget(
                                () -> new ReversiTutorialWidget(() -> {
                                    BitboardReversi game = new BitboardReversi(players);
                                    gameController = new GenericGameController<>(new ReversiBitCanvas(), game, new LocalThreadBehaviour<>(game), "Reversi");
                                    gameController.start();
                                }),
                                () -> Platform.runLater(() -> {
                                    BitboardReversi game = new BitboardReversi(players);
                                    gameController = new GenericGameController<>(new ReversiBitCanvas(), game, new LocalThreadBehaviour<>(game), "Reversi");
                                    gameController.start();
                                }),
                                () -> AppSettings.getSettings().setFirstReversi(false)
                        );
                    } else {
                        BitboardReversi game = new BitboardReversi(players);
                        gameController = new GenericGameController<>(new ReversiBitCanvas(), game, new LocalThreadBehaviour<>(game), "Reversi");
                        gameController.start();
                    }
                    break;
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