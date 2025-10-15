package org.toop.app.view.views;

import org.toop.app.GameInformation;
import org.toop.app.game.ReversiGame;
import org.toop.app.game.TicTacToeGame;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class LocalMultiplayerView extends View {
	private final GameInformation information;

	public LocalMultiplayerView(GameInformation information) {
		super(true, "bg-primary");
		this.information = information;
	}

	public LocalMultiplayerView(GameInformation.Type type) {
		this(new GameInformation(type));
	}

	@Override
	public void setup() {
		final Button playButton = button();
		playButton.setText(AppContext.getString("play"));
		playButton.setOnAction(_ -> {
			for (final GameInformation.Player player : information.players) {
				if (player.name.isEmpty()) {
					ViewStack.push(new ErrorView(AppContext.getString("please-enter-your-name")));
					return;
				}
			}

			switch (information.type) {
				case TICTACTOE: new TicTacToeGame(information, 0, null, null); break;
				case REVERSI: new ReversiGame(information, 0, null, null); break;
			}
		});

		add(Pos.CENTER,
			fit(vboxFill(
				hbox(
					setupPlayers()
				),

				separator(),
				playButton
			))
		);

		final Button backButton = button();
		backButton.setText(AppContext.getString("back"));
		backButton.setOnAction(_ -> { ViewStack.push(new MainView()); });

		add(Pos.BOTTOM_LEFT,
			vboxFill(
				backButton
			)
		);
	}

	private VBox[] setupPlayers() {
		final VBox[] playerBoxes = new VBox[GameInformation.Type.playerCount(information.type)];

		for (int i = 0; i < playerBoxes.length; i++) {
			final int index = i;

			List<Node> nodes = new ArrayList<>();

			final Text playerHeader = header();
			playerHeader.setText(AppContext.getString("player") + " #" + (i + 1));

			nodes.add(playerHeader);
			nodes.add(separator());

			final Text nameText = text();
			nameText.setText(AppContext.getString("name"));

			if (information.players[i].isHuman) {
				final Button playerToggle = button();
				playerToggle.setText(AppContext.getString("player"));
				playerToggle.setOnAction(_ -> {
					information.players[index].isHuman = false;
					cleanup();
					setup();
				});

				nodes.add(vboxFill(playerToggle));

				final TextField playerNameInput = input();
				playerNameInput.setPromptText(AppContext.getString("enter-your-name"));
				playerNameInput.setText(information.players[i].name);
				playerNameInput.textProperty().addListener((_, _, newValue) -> {
					information.players[index].name = newValue;
				});

				nodes.add(vboxFill(nameText, playerNameInput));
			} else {
				final Button computerToggle = button();
				computerToggle.setText(AppContext.getString("computer"));
				computerToggle.setOnAction(_ -> {
					information.players[index].isHuman = true;
					cleanup();
					setup();
				});

				nodes.add(vboxFill(computerToggle));

				information.players[i].name = "Pism Bot V" + i;

				final Text computerNameText = text();
				computerNameText.setText(information.players[index].name);

				nodes.add(vboxFill(nameText, computerNameText));

				final Text computerDifficultyText = text();
				computerDifficultyText.setText(AppContext.getString("computer-difficulty"));

				final Slider computerDifficultySlider = slider();
				computerDifficultySlider.setMin(0);
				computerDifficultySlider.setMax(GameInformation.Type.maxDepth(information.type));
				computerDifficultySlider.setValue(information.players[i].computerDifficulty);
				computerDifficultySlider.valueProperty().addListener((_, _, newValue) -> {
					information.players[index].computerDifficulty = newValue.intValue();
				});

				nodes.add(vboxFill(computerDifficultyText, computerDifficultySlider));

				final Text computerThinkTimeText = text();
				computerThinkTimeText.setText(AppContext.getString("computer-think-time"));

				final Slider computerThinkTimeSlider = slider();
				computerThinkTimeSlider.setMin(0);
				computerThinkTimeSlider.setMax(5);
				computerThinkTimeSlider.setValue(information.players[i].computerThinkTime);
				computerThinkTimeSlider.valueProperty().addListener((_, _, newValue) -> {
					information.players[index].computerThinkTime = newValue.intValue();
				});

				nodes.add(vboxFill(computerThinkTimeText, computerThinkTimeSlider));
			}

			playerBoxes[i] = vboxFill(nodes.toArray(new Node[0]));
		}

		return playerBoxes;
	}
}