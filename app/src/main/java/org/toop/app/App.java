package org.toop.app;

import org.toop.app.layer.Layer;
import org.toop.app.layer.layers.MainLayer;
import org.toop.app.layer.layers.QuitLayer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.CssAsset;

public final class App extends Application {
	private static Stage stage;
	private static StackPane root;

	private static int width;
	private static int height;

	private static boolean isQuitting;

	public static void run(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		final StackPane root = new StackPane();

		final Scene scene = new Scene(root);
		scene.getStylesheets().add(ResourceManager.get(CssAsset.class, "app.css").getUrl());

		stage.setTitle("pism");
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
		App.root = root;

		App.width = (int)stage.getWidth();
		App.height = (int)stage.getHeight();

		App.isQuitting = false;

		push(new MainLayer());
    }

	public static void push(Layer layer) {
        root.getChildren().addLast(layer.getLayer());
	}

	public static void pop() {
		root.getChildren().removeLast();
		isQuitting = false;
	}

	public static void quitPopup() {
		push(new QuitLayer());
		isQuitting = true;
	}

	public static void quit() {
		stage.close();
	}

	public static int getWidth() { return width; }
	public static int getHeight() { return height; }
}