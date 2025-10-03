package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.toop.app.App;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.local.AppContext;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Locale;

public final class OptionsMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
    private LocalizationAsset loc = ResourceManager.get("localization");

    public OptionsMenu() {
        final Region background = createBackground();

        GraphicsDevice currentScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

        LocalizationAsset locFiles = ResourceManager.get(LocalizationAsset.class, "localization");
        final Label selectLanguageLabel = new Label(loc.getString("optionsMenuLabelSelectLanguage", currentLocale));
        final ChoiceBox<Locale> selectLanguage = new ChoiceBox<>();
        selectLanguage.setValue(currentLocale);
        for (Locale locFile : locFiles.getAvailableLocales()) {
            selectLanguage.getItems().add(locFile);
        }

        selectLanguage.setOnAction((event) -> {
            Locale selectedLocale = selectLanguage.getSelectionModel().getSelectedItem();
            AppContext.setLocale(selectedLocale);
            App.pop();
            App.push(new OptionsMenu());
        });

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice[] devices = ge.getScreenDevices();
//        final ChoiceBox<GraphicsDevice> selectScreen = new ChoiceBox<>();
//        for (GraphicsDevice screen : devices) {
//            selectScreen.getItems().add(screen);
//        }
//
//        selectScreen.setOnAction((event) -> {
//            int selectedIndex = selectScreen.getSelectionModel().getSelectedIndex();
//            Object selectedItem = selectScreen.getSelectionModel().getSelectedItem();
//
//            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
//            System.out.println("   ChoiceBox.getValue(): " + selectScreen.getValue());
//        });
//
//        final ChoiceBox<DisplayMode> selectWindowSize = new ChoiceBox<>();
//        for (DisplayMode displayMode : currentScreen.getDisplayModes()) {
//            selectWindowSize.getItems().add(displayMode);
//        }
//
////        if (currentScreen.isFullScreenSupported()) {}
//        final CheckBox setFullscreen = new CheckBox("Fullscreen");

        final VBox optionsBox = new VBox(10, selectLanguageLabel, selectLanguage);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPickOnBounds(false);
        optionsBox.setTranslateY(50);
        optionsBox.setTranslateX(25);

        final Button credits = createButton("Credits", () -> { App.push(new CreditsMenu()); });
        final Button options = createButton("Exit Options", () -> { App.push(new MainMenu()); });
        final Button quit = createButton("Quit", () -> { App.quitPopup(); });

        final VBox controlBox = new VBox(10, credits, options, quit);
        controlBox.setAlignment(Pos.BOTTOM_LEFT);
        controlBox.setPickOnBounds(false);
        controlBox.setTranslateY(-50);
        controlBox.setTranslateX(25);

        pane = new StackPane(background, optionsBox, controlBox);
    }
}