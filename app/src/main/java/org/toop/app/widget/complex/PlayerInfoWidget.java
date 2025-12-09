package org.toop.app.widget.complex;

import org.toop.app.GameInformation;
import org.toop.app.widget.Primitive;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PlayerInfoWidget {
	private final GameInformation.Player information;
	private final VBox container;
    private Text playerName;
    private boolean hasSet;

	public PlayerInfoWidget(GameInformation.Player information) {
		this.information = information;
		container = Primitive.vbox(
			buildToggle().getNode(),
			buildContent()
		);
        this.playerName = null;
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
            var spacer = Primitive.vbox(
                    makeAIButton(0, 0, "zwartepiet"),
                    makeAIButton(0, 0, "sinterklaas"),
                    makeAIButton(0, 0, "santa")
            );                                                                  //todo make a better solution
            spacer.setVisible(false);
            var nameInput = new LabeledInputWidget(
                    "name",
                    "enter-your-name",
                    information.name,
                    newName -> information.name = newName
            );

            return Primitive.vbox(spacer,nameInput.getNode());
        } else {
            var AIBox = Primitive.vbox(
                    makeAIButton(0, 1, "zwartepiet"),
                    makeAIButton(2, 1, "sinterklaas"),
                    makeAIButton(9, 1, "santa")
            );

            this.playerName = Primitive.text("");
            playerName.setText(information.name);

            var nameDisplay = Primitive.vbox(
                    Primitive.text("name"),
                    playerName
            );

            if (!hasSet) {
                doDefault();
                hasSet = true;
            }

            return Primitive.vbox(
                    AIBox,
                    nameDisplay
            );

        }
    }

	public Node getNode() {
		return container;
	}

    private Node makeAIButton(int depth, int thinktime, String name) {
        return Primitive.button(name, () -> {
            information.name = getName(name);
            information.computerDifficulty = depth;
            information.computerThinkTime = thinktime;
            this.playerName.setText(getName(name));
        });
    }

    private String getName(String name) {
        return switch (name) {
            case "sinterklaas" -> "Sint. R. Klaas";
            case "zwartepiet" -> "Zwarte Piet";
            case "santa" -> "Santa";
            default -> "Default";
        };
    }

    private void doDefault() {
        information.name = getName("zwartepiet");
        information.computerDifficulty = 0;
        information.computerThinkTime = 1;
        this.playerName.setText(getName("zwartepiet"));
    }
}