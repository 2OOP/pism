package org.toop.app.menu;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.toop.app.GameType;

public class GameSelectMenu extends Menu {
	public GameSelectMenu(GameType type) {
		final Region background = createBackground();

		final ComboBox<String> selectedGame = new ComboBox<>();
		selectedGame.getItems().add(GameType.toName(GameType.TICTACTOE));
		selectedGame.getItems().add(GameType.toName(GameType.REVERSI));
		selectedGame.setValue(GameType.toName(type));

		final ComboBox<String> selectedMode = new ComboBox<>();
		selectedMode.getItems().add("Local");
		selectedMode.getItems().add("Online");
		selectedMode.setValue("Local");

		final HBox selectedContainer = new HBox(10, selectedGame, selectedMode);

		final TextField serverIpField = new TextField();
		serverIpField.setPromptText("Enter here your server ip address");

		VBox box = new VBox(selectedContainer, serverIpField);
		pane = new StackPane(background, box);
	}
}