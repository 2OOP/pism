package org.toop.app.widget.complex;

import org.toop.app.GameInformation;
import org.toop.app.widget.Primitive;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class PlayerInfoWidget {
	private final GameInformation.Player information;
	private final VBox container;

	public PlayerInfoWidget(GameInformation.Player information) {
		this.information = information;
		container = Primitive.vbox(
			buildToggle().getNode(),
			buildContent()
		);
	}

	private ToggleWidget buildToggle() {
		return new ToggleWidget(
			"computer", "player",
			information.isHuman,
			isHuman -> {
				information.isHuman = isHuman;
				container.getChildren().setAll(
					buildToggle().getNode(),
					buildContent()
				);
			}
		);
	}

	private Node buildContent() {
		if (information.isHuman) {
			var nameInput = new LabeledInputWidget(
				"name",
				"enter-your-name",
				information.name,
				newName -> information.name = newName
			);

			return nameInput.getNode();
		} else {
			if (information.name == null || information.name.isEmpty()) {
				information.name = "Pism Bot";
			}

			var playerName = Primitive.text("");
			playerName.setText(information.name);

			var nameDisplay = Primitive.vbox(
				Primitive.text("name"),
				playerName
			);

			var difficultySlider = new LabeledSliderWidget(
				"computer-difficulty",
				0, 5,
				information.computerDifficulty,
				newVal -> information.computerDifficulty = newVal
			);

			var thinkTimeSlider = new LabeledSliderWidget(
				"computer-think-time",
				0, 5,
				information.computerThinkTime,
				newVal -> information.computerThinkTime = newVal
			);

			return Primitive.vbox(
				nameDisplay,
				difficultySlider.getNode(),
				thinkTimeSlider.getNode()
			);
		}
	}

	public Node getNode() {
		return container;
	}
}