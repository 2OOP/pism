package org.toop.app.menu;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.toop.app.GameType;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;
import org.toop.local.LocalizationEvents;

import java.util.Locale;

public class GameSelectMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
    private final LocalizationAsset loc = ResourceManager.get("localization.properties");

    final ComboBox<String> selectedMode, selectedGame;
    final TextField serverIpField;

	public GameSelectMenu(GameType type) {
		final Region background = createBackground();

        selectedGame = new ComboBox<>();
		selectedGame.getItems().add(GameType.toName(GameType.TICTACTOE));
		selectedGame.getItems().add(GameType.toName(GameType.REVERSI));
		selectedGame.setValue(GameType.toName(type));

		selectedMode = new ComboBox<>();
		selectedMode.getItems().add("Local");
		selectedMode.getItems().add("Online");
		selectedMode.setValue("Local");

		final HBox selectedContainer = new HBox(10, selectedGame, selectedMode);

		serverIpField = new TextField();
        serverIpField.setPromptText(loc.getString("gameSelectMenuEnterIP",currentLocale));

		VBox box = new VBox(selectedContainer, serverIpField);
		pane = new StackPane(background, box);
        try {
            new EventFlow()
                    .listen(this::handleChangeLanguage);

        }catch (Exception e){
            System.out.println("Something went wrong while trying to change the language.");
            throw e;
        }

    }
    private void handleChangeLanguage(LocalizationEvents.LanguageHasChanged event) {
        Platform.runLater(() -> {
            currentLocale = AppContext.getLocale();
            serverIpField.setPromptText(loc.getString("gameSelectMenuEnterIP",currentLocale));
            selectedGame.getItems().set(0, loc.getString("mainMenuSelectTicTacToe",currentLocale));
            selectedGame.getItems().set(1, loc.getString("mainMenuSelectReversi",currentLocale));
            selectedMode.getItems().set(0, loc.getString("gameSelectMenuLocal",currentLocale));
            selectedMode.getItems().set(1, loc.getString("gameSelectMenuOnline",currentLocale));
        });

    }
}