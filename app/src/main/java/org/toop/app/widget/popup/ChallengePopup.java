package org.toop.app.widget.popup;

import org.toop.app.GameInformation;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.PlayerInfoWidget;
import org.toop.app.widget.complex.PopupWidget;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import org.toop.local.AppContext;

public final class ChallengePopup extends PopupWidget {
	private final GameInformation.Player playerInformation;
	private final String challenger;
	private final String game;
	private final Consumer<GameInformation.Player> onAccept;

	public ChallengePopup(String challenger, String game, Consumer<GameInformation.Player> onAccept) {
		this.challenger = challenger;
		this.game = game;
		this.onAccept = onAccept;

		this.playerInformation = new GameInformation.Player();

		setupLayout();
	}

	private void setupLayout() {
		var challengeText = Primitive.text("you-were-challenged-by");

		var challengerHeader = Primitive.header(challenger, false);

		var gameText = Primitive.text(AppContext.getString("to-a-game-of") + " " + game, false);

		var acceptButton = Primitive.button("accept", () -> {
			onAccept.accept(playerInformation);
			this.hide();
		});
		var denyButton = Primitive.button("deny", () -> hide());

		var leftSection = Primitive.vbox(
			challengeText,
			challengerHeader,
			gameText,
			Primitive.separator(),
			Primitive.hbox(
				acceptButton,
				denyButton
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