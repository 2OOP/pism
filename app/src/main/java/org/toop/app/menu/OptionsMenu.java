package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.toop.app.App;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
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
                volumeSelectorCreation(),
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

        selectLanguage.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Locale locale) {
                return locale.getDisplayName();
            }

            @Override
            public Locale fromString(String string) {
                return null;
            }
        });

        selectLanguage.setOnShowing(event -> {
            new EventFlow().addPostEvent(new AudioEvents.clickButton()).asyncPostEvent();
        });

        selectLanguage.setOnAction(event -> {
            new EventFlow().addPostEvent(new AudioEvents.clickButton()).asyncPostEvent();
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

        selectScreen.setOnShowing(event -> {
            new EventFlow().addPostEvent(new AudioEvents.clickButton()).asyncPostEvent();
        });

        selectScreen.setOnAction(event -> {
            new EventFlow().addPostEvent(new AudioEvents.clickButton()).asyncPostEvent();
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
        selectWindowSize.setOnShowing(event -> {
            new EventFlow().addPostEvent(new AudioEvents.clickButton()).asyncPostEvent();
        });
        selectWindowSize.setOnAction(event -> {
            new EventFlow().addPostEvent(new AudioEvents.clickButton()).asyncPostEvent();
            int selectedIndex = selectWindowSize.getSelectionModel().getSelectedIndex();
            Object selectedItem = selectWindowSize.getSelectionModel().getSelectedItem();

            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
            System.out.println("   ChoiceBox.getValue(): " + selectWindowSize.getValue());
        });
        return selectWindowSize;
    }

    private CheckBox selectFullscreenCreation() {
        final CheckBox setFullscreen = new CheckBox("Fullscreen");
        setFullscreen.setSelected(App.isFullscreen());
        setFullscreen.setOnAction(event -> {
            new EventFlow().addPostEvent(new AudioEvents.clickButton()).asyncPostEvent();
            boolean isSelected = setFullscreen.isSelected();
            App.setFullscreen(isSelected);
        });
        return setFullscreen;
    }

    private Slider volumeSelectorCreation() {
        Slider volumeSlider = new Slider(0, 100, 50);
        new EventFlow()
                .addPostEvent(AudioEvents.GetCurrentVolume.class)
                .onResponse(AudioEvents.GetCurrentVolumeReponse.class, event -> {
                    volumeSlider.setValue(event.currentVolume() * 100);
                }, true).asyncPostEvent();
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(5);
        volumeSlider.setMaxWidth(225);

        Label valueLabel = new Label(String.valueOf((int) volumeSlider.getValue()));

        final long[] lastPlayed = {0};
        final long cooldown = 50;
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            long now = System.currentTimeMillis();

            if (now - lastPlayed[0] >= cooldown) {
                lastPlayed[0] = now;
//                new EventFlow().addPostEvent(new AudioEvents.clickButton())
//                        .asyncPostEvent(); // TODO: creates double sound bug, WHYYY????
            }
            valueLabel.setText(String.valueOf(newVal.intValue()));
            new EventFlow().addPostEvent(new AudioEvents.ChangeVolume(newVal.doubleValue()/100.0))
                    .asyncPostEvent();
        });

        return volumeSlider;
    }

}