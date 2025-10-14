package org.toop.app;

import org.toop.app.view.ViewStack;
import org.toop.app.view.views.MainView;
import org.toop.app.view.views.QuitView;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.CssAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;
import org.toop.local.AppSettings;

import javafx.application.Application;
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
        final StackPane root = new StackPane();
		final Scene scene = new Scene(root);
		ViewStack.setup(scene);

		stage.setTitle(AppContext.getString("app-title"));
		stage.setWidth(1080);
		stage.setHeight(720);

		stage.setOnCloseRequest(event -> {
			event.consume();
			startQuit();
		});

		stage.setScene(scene);
		stage.setResizable(false);

		stage.show();

		App.stage = stage;
		App.scene = scene;

		App.width = (int)stage.getWidth();
		App.height = (int)stage.getHeight();

		App.isQuitting = false;

		AppSettings.applySettings();
		new EventFlow().addPostEvent(new AudioEvents.StartBackgroundMusic()).asyncPostEvent();

		ViewStack.push(new MainView());
	}

	public static void startQuit() {
		if (isQuitting) {
			return;
		}

		ViewStack.push(new QuitView());
		isQuitting = true;
	}

	public static void stopQuit() {
		ViewStack.pop();
		isQuitting = false;
	}

	public static void quit() {
		ViewStack.cleanup();
		stage.close();
	}

	public static void reload() {
		stage.setTitle(AppContext.getString("app-title"));
		ViewStack.reload();
	}

	public static void setFullscreen(boolean fullscreen) {
		stage.setFullScreen(fullscreen);

		width = (int) stage.getWidth();
		height = (int) stage.getHeight();

		reload();
	}

	public static void setStyle(String theme, String layoutSize) {
		final int stylesCount = scene.getStylesheets().size();

		for (int i = 0; i < stylesCount; i++) {
			scene.getStylesheets().removeLast();
		}

		scene.getStylesheets().add(ResourceManager.<CssAsset>get("general.css").getUrl());
		scene.getStylesheets().add(ResourceManager.<CssAsset>get(theme + ".css").getUrl());
		scene.getStylesheets().add(ResourceManager.<CssAsset>get(layoutSize + ".css").getUrl());

		reload();
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}
}