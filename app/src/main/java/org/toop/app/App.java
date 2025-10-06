package org.toop.app;

import org.toop.app.layer.Layer;
import org.toop.app.layer.layers.MainLayer;
import org.toop.app.layer.layers.QuitPopup;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.CssAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.toop.local.AppSettings;

import java.util.Stack;

public final class App extends Application {
	private static Stage stage;
	private static Scene scene;
	private static StackPane root;

	private static Stack<Layer> stack;
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

		stage.setTitle(AppContext.getString("appTitle"));
		stage.setWidth(1080);
		stage.setHeight(720);

		stage.setOnCloseRequest(event -> {
			event.consume();

			if (!isQuitting) {
				quitPopup();
			}
		});

		stage.setScene(scene);
		stage.setResizable(false);

		stage.show();

		App.stage = stage;
		App.scene = scene;
		App.root = root;

		App.stack = new Stack<>();

		App.width = (int) stage.getWidth();
		App.height = (int) stage.getHeight();

		App.isQuitting = false;

		final AppSettings settings = new AppSettings();
		settings.applySettings();

		new EventFlow().addPostEvent(new AudioEvents.StartBackgroundMusic()).asyncPostEvent();
		activate(new MainLayer());
	}

	public static void activate(Layer layer) {
		popAll();
		push(layer);
	}

	public static void push(Layer layer) {
		root.getChildren().addLast(layer.getLayer());
		stack.push(layer);
	}

	public static void pop() {
		root.getChildren().removeLast();
		stack.pop();

		isQuitting = false;
	}

	public static void popAll() {
		final int childrenCount = root.getChildren().size();

		for (int i = 0; i < childrenCount; i++) {
			root.getChildren().removeLast();
		}

		stack.removeAllElements();
	}

	public static void quitPopup() {
		push(new QuitPopup());
		isQuitting = true;
	}

	public static void quit() {
		stage.close();
	}

	public static void reloadAll() {
		stage.setTitle(AppContext.getString("appTitle"));

		for (final Layer layer : stack) {
			layer.reload();
		}
	}

	public static void setFullscreen(boolean fullscreen) {
		stage.setFullScreen(fullscreen);

		width = (int) stage.getWidth();
		height = (int) stage.getHeight();

		reloadAll();
	}

	public static void setStyle(String theme, String layoutSize) {
		final int stylesCount = scene.getStylesheets().size();

		for (int i = 0; i < stylesCount; i++) {
			scene.getStylesheets().removeLast();
		}

		scene.getStylesheets().add(ResourceManager.<CssAsset>get(theme + ".css").getUrl());
		scene.getStylesheets().add(ResourceManager.<CssAsset>get(layoutSize + ".css").getUrl());

		reloadAll();
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}
}