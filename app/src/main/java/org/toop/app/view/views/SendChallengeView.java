package org.toop.app.view.views;

import org.toop.app.GameInformation;
import org.toop.app.Server;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class SendChallengeView extends View {
	private final Server server;
	private final String opponent;
	private final BiConsumer<GameInformation.Player, String> onSend;

	private final GameInformation.Player playerInformation;

	public SendChallengeView(Server server, String opponent, BiConsumer<GameInformation.Player, String> onSend) {
		super(false, "bg-popup");

		this.server = server;
		this.opponent = opponent;
		this.onSend = onSend;

		playerInformation = new GameInformation.Player();
	}

	@Override
	public void setup() {
		final Text challengeText = text();
		challengeText.setText(AppContext.getString("challenge"));

		final Text opponentHeader = header();
		opponentHeader.setText(opponent);

		final Text gameText = text();
		gameText.setText(AppContext.getString("to-a-game-of"));

		final ComboBox<String> gamesCombobox = combobox();
		gamesCombobox.getItems().addAll(server.getGameList());
		gamesCombobox.setValue(gamesCombobox.getItems().getFirst());

		final Button sendButton = button();
		sendButton.setText(AppContext.getString("send"));
		sendButton.setOnAction(_ -> { onSend.accept(playerInformation, gamesCombobox.getValue()); });

		final Button cancelButton = button();
		cancelButton.setText(AppContext.getString("cancel"));
		cancelButton.setOnAction(_ -> {
            ViewStack.pop(); });

		final List<Node> nodes = new ArrayList<>();

		if (playerInformation.isHuman) {
			final Button playerToggle = button();
			playerToggle.setText(AppContext.getString("player"));
			playerToggle.setOnAction(_ -> {
				playerInformation.isHuman = false;
				cleanup();
				setup();
			});

			nodes.add(vbox(playerToggle));
		} else {
			final Button computerToggle = button();
			computerToggle.setText(AppContext.getString("computer"));
			computerToggle.setOnAction(_ -> {
				playerInformation.isHuman = true;
				cleanup();
				setup();
			});

			nodes.add(vbox(computerToggle));

			final Text computerDifficultyText = text();
			computerDifficultyText.setText(AppContext.getString("computer-difficulty"));

			final Slider computerDifficultySlider = slider();
			computerDifficultySlider.setMin(0);
			computerDifficultySlider.setMax(Server.gameToType(gamesCombobox.getValue()).getMaxDepth());
			computerDifficultySlider.setValue(playerInformation.computerDifficulty);
			computerDifficultySlider.valueProperty().addListener((_, _, newValue) -> {
				playerInformation.computerDifficulty = newValue.intValue();
			});

			nodes.add(vbox(computerDifficultyText, computerDifficultySlider));
		}

		add(Pos.CENTER,
			fit(hboxFill(
				vboxFill(
					challengeText,
					opponentHeader,
					gameText,
					gamesCombobox,
					separator(),

					hboxFill(
						sendButton,
						cancelButton
					)
				),

				vboxFill(
					nodes.toArray(new Node[0])
				)
			))
		);
	}
}