package org.toop.app.widget.popup;

import org.toop.app.GameInformation;
import org.toop.app.Server;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.LabeledChoiceWidget;
import org.toop.app.widget.complex.PlayerInfoWidget;
import org.toop.app.widget.complex.PopupWidget;
import org.toop.local.AppContext;

import java.util.function.BiConsumer;

import javafx.geometry.Pos;
import javafx.util.StringConverter;

public final class SendChallengePopup extends PopupWidget {
	private final Server server;
	private final String opponent;
	private final BiConsumer<GameInformation.Player, String> onSend;

	private final GameInformation.Player playerInformation;

	public SendChallengePopup(Server server, String opponent, BiConsumer<GameInformation.Player, String> onSend) {
		this.server = server;
		this.opponent = opponent;
		this.onSend = onSend;

		this.playerInformation = new GameInformation.Player();

		setupLayout();
	}

	private void setupLayout() {
		// --- Left side: challenge text and buttons ---
		var challengeText = Primitive.text("challenge");

		var opponentHeader = Primitive.header(opponent);

		var gameText = Primitive.text("to-a-game-of");

		var games = server.getGameList();
		var gameChoice = new LabeledChoiceWidget<>(
			"game",
			new StringConverter<>() {
				@Override
				public String toString(String game) {
					return AppContext.getString(game);
				}
				@Override
				public String fromString(String s) { return null; }
			},
			games.getFirst(),
			newGame -> {
				playerInformation.computerDifficulty = Math.min(
					playerInformation.computerDifficulty,
					Server.gameToType(newGame).getMaxDepth()
				);
            },
			games.toArray(new String[0])
		);

		var sendButton = Primitive.button(
			"send",
			() -> onSend.accept(playerInformation, gameChoice.getValue())
		);

		var cancelButton = Primitive.button("cancel", () -> hide());

		var leftSection = Primitive.vbox(
			challengeText,
			opponentHeader,
			gameText,
			gameChoice.getNode(),
			Primitive.separator(),
			Primitive.hbox(
				sendButton,
				cancelButton
			)
		);

		var playerInfoWidget = new PlayerInfoWidget(playerInformation);

		add(Pos.CENTER,
			Primitive.hbox(
				leftSection,
				playerInfoWidget.getNode()
			)
		);
	}
}