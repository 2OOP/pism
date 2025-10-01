package org.toop.app.menu;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.toop.app.GameType;

public class GameMenu extends Menu {
	public GameMenu(GameType type) {
		final Region background = createBackground();

		ComboBox<String> selectedGame = new ComboBox<>();

		TextField serverIpField = new TextField();
		serverIpField.setPromptText("Enter here your server ip address");

		VBox box = new VBox(selectedGame, serverIpField);
		pane = new StackPane(background, box);
	}
}