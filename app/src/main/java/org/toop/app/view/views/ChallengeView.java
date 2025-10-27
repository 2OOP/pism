package org.toop.app.view.views;

import org.toop.app.GameInformation;
import org.toop.app.Server;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.app.view.displays.SongDisplay;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ChallengeView extends View {
	private final GameInformation.Player playerInformation;

	private final String challenger;
	private final String game;

	private final Consumer<GameInformation.Player> onAccept;

	public ChallengeView(String challenger, String game, Consumer<GameInformation.Player> onAccept) {
		super(false, "bg-popup");

		playerInformation = new GameInformation.Player();

		this.challenger = challenger;
		this.game = game;

		this.onAccept = onAccept;
	}

	@Override
	public void setup() {
		final Text challengeText = text();
		challengeText.setText(AppContext.getString("you-were-challenged-by"));

		final Text challengerHeader = header();
		challengerHeader.setText(challenger);

		final Text gameText = text();
		gameText.setText(AppContext.getString("to-a-game-of") + " " + game);

		final Button acceptButton = button();
		acceptButton.setText(AppContext.getString("accept"));
		acceptButton.setOnAction(_ -> {
			onAccept.accept(playerInformation);
		});

		final Button denyButton = button();
		denyButton.setText(AppContext.getString("deny"));
		denyButton.setOnAction(_ -> {
            ViewStack.pop();
		});

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
			computerDifficultySlider.setMax(GameInformation.Type.maxDepth(Server.gameToType(game)));
			computerDifficultySlider.setValue(playerInformation.computerDifficulty);
			computerDifficultySlider.valueProperty().addListener((_, _, newValue) -> {
				playerInformation.computerDifficulty = newValue.intValue();
			});

			nodes.add(vbox(computerDifficultyText, computerDifficultySlider));
		}

        final SongDisplay songdisplay = new SongDisplay();


        add(Pos.BOTTOM_RIGHT,
                fit(vboxFill(
                        songdisplay
                )));

		add(Pos.CENTER,
			fit(hboxFill(
				vboxFill(
					challengeText,
					challengerHeader,
					gameText,
					separator(),

					hboxFill(
						acceptButton,
						denyButton
					)
				),

				vboxFill(
					nodes.toArray(new Node[0])
				)
			))
		);
	}
}