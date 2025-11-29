package org.toop.app.widget.view;

import org.toop.app.GameInformation;
import org.toop.app.game.*;
import org.toop.app.game.Players.ArtificialPlayer;
import org.toop.app.game.Players.LocalPlayer;
import org.toop.app.game.Players.Player;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.PlayerInfoWidget;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.app.widget.popup.ErrorPopup;
import org.toop.app.game.TurnBasedGameThread;
import org.toop.game.tictactoe.TicTacToeAIR;
import org.toop.game.tictactoe.TicTacToeR;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

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
				case TICTACTOE -> new TicTacToeController(new Player[]{new LocalPlayer(), new ArtificialPlayer<>(new TicTacToeAIR())});
				case REVERSI -> new ReversiGame(information);
				case CONNECT4 -> new Connect4Game(information);
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