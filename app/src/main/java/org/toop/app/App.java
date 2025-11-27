package org.toop.app;

import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.display.SongDisplay;
import org.toop.app.widget.popup.QuitPopup;
import org.toop.app.widget.view.MainView;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.CssAsset;
import org.toop.local.AppContext;
import org.toop.local.AppSettings;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class App extends Application {
	private static Stage stage;
	private static Scene scene;

    private static int height;
    private static int width;

	private static boolean isQuitting;

	public static void run(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
        final StackPane root = WidgetContainer.setup();
		final Scene scene = new Scene(root);

		stage.setTitle(AppContext.getString("app-title"));
		stage.titleProperty().bind(AppContext.bindToKey("app-title"));

		stage.setWidth(1080);
		stage.setHeight(720);

        stage.setMinWidth(1080);
        stage.setMinHeight(720);
		stage.setOnCloseRequest(event -> {
			event.consume();
			startQuit();
		});

		stage.setScene(scene);
		stage.setResizable(true);

		stage.show();

		App.stage = stage;
		App.scene = scene;

		App.width = (int)stage.getWidth();
		App.height = (int)stage.getHeight();

		App.isQuitting = false;

		AppSettings.applySettings();
		new EventFlow().addPostEvent(new AudioEvents.StartBackgroundMusic()).asyncPostEvent();

        WidgetContainer.add(Pos.CENTER, new MainView());
		WidgetContainer.add(Pos.BOTTOM_RIGHT, new SongDisplay());
	}

	public static void startQuit() {
		if (isQuitting) {
			return;
		}

		WidgetContainer.add(Pos.CENTER, new QuitPopup());
		isQuitting = true;
	}

	public static void stopQuit() {
		isQuitting = false;
	}

	public static void quit() {
		stage.close();
		System.exit(0); // TODO: This is like dropping a nuke
	}

	public static void setFullscreen(boolean fullscreen) {
		stage.setFullScreen(fullscreen);

		width = (int)stage.getWidth();
		height = (int)stage.getHeight();
	}

	public static void setStyle(String theme, String layoutSize) {
		scene.getStylesheets().clear();

		scene.getStylesheets().add(ResourceManager.<CssAsset>get("general.css").getUrl());
		scene.getStylesheets().add(ResourceManager.<CssAsset>get(theme + ".css").getUrl());
		scene.getStylesheets().add(ResourceManager.<CssAsset>get(layoutSize + ".css").getUrl());
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}
}