package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.toop.app.App;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.local.AppContext;

import java.awt.*;
import java.util.Locale;

public final class OptionsMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
    private LocalizationAsset loc = ResourceManager.get("localization");
    private GraphicsDevice currentScreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

    public OptionsMenu() {
        final Label selectLanguageLabel = new Label(
                loc.getString("optionsMenuLabelSelectLanguage", currentLocale)
        );

        final Button exitOptionsButton = createButton("Exit Options", () -> { App.pop(); } );

        final VBox optionsBox = new VBox(10,
                selectLanguageLabel,
                languageSelectorCreation(),
                screenDeviceSelectorCreation(),
                displayModeSelectorCreation(),
                selectFullscreenCreation(),
                exitOptionsButton);

        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPickOnBounds(false);
        optionsBox.setTranslateY(50);
        optionsBox.setTranslateX(25);

        pane = new StackPane(optionsBox);

    }

    private ChoiceBox<Locale> languageSelectorCreation() {
        final ChoiceBox<Locale> selectLanguage = new ChoiceBox<>();
        selectLanguage.setValue(currentLocale);

        for (Locale locFile : loc.getAvailableLocales()) {
            selectLanguage.getItems().add(locFile);
        }

        selectLanguage.setConverter(new javafx.util.StringConverter<Locale>() {
            @Override
            public String toString(Locale locale) {
                return locale.getDisplayName();
            }

            @Override
            public Locale fromString(String string) {
                return null;
            }
        });

        selectLanguage.setOnAction(event -> {
            Locale selectedLocale = selectLanguage.getSelectionModel().getSelectedItem();
            if (selectedLocale != null) {
                AppContext.setLocale(selectedLocale);
                App.pop();
                App.push(new OptionsMenu());
            }
        });

        return selectLanguage;
    }

    private ChoiceBox<GraphicsDevice> screenDeviceSelectorCreation() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();
        final ChoiceBox<GraphicsDevice> selectScreen = new ChoiceBox<>();
        for (GraphicsDevice screen : devices) {
            selectScreen.getItems().add(screen);
        }

        selectScreen.setOnAction(event -> {
            int selectedIndex = selectScreen.getSelectionModel().getSelectedIndex();
            Object selectedItem = selectScreen.getSelectionModel().getSelectedItem();

            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
            System.out.println("   ChoiceBox.getValue(): " + selectScreen.getValue());
        });
        return selectScreen;
    }

    private ChoiceBox<DisplayMode> displayModeSelectorCreation() {
        final ChoiceBox<DisplayMode> selectWindowSize = new ChoiceBox<>();
        for (DisplayMode displayMode : currentScreenDevice.getDisplayModes()) {
            selectWindowSize.getItems().add(displayMode);
        }
        return selectWindowSize;
    }

    private CheckBox selectFullscreenCreation() {
        final CheckBox setFullscreen = new CheckBox("Fullscreen");
        setFullscreen.setSelected(App.isFullscreen());
        setFullscreen.setOnAction(event -> {
            boolean isSelected = setFullscreen.isSelected();
            App.setFullscreen(isSelected);
        });
        return setFullscreen;
    }

}