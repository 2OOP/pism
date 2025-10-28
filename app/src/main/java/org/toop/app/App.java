package org.toop.app;

import javafx.geometry.Pos;
import org.toop.app.view.ViewStack;
import org.toop.app.view.views.QuitView;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.ConfirmWidget;
import org.toop.app.widget.complex.PopupWidget;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.CssAsset;
import org.toop.local.AppContext;
import org.toop.local.AppSettings;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;

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
		stage.setWidth(1080);
		stage.setHeight(720);

		scene.getRoot();

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

        var abc = new ConfirmWidget("abc");
        var cab = new ConfirmWidget("cab");

        abc.addButton("test", () -> {
            WidgetContainer.add(Pos.CENTER, cab);
            WidgetContainer.remove(abc);
        });
        abc.addButton("test3333", () -> IO.println("Second test works!"));

        cab.addButton("cab321312", () -> IO.println("Third test"));
        cab.addButton("cab31232132131", () -> {
            IO.println("Fourth test");
            WidgetContainer.remove(cab);
        });

        WidgetContainer.add(Pos.CENTER, abc);
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
		System.exit(0); // TODO: This is like dropping a nuke
	}

	public static void reload() {
		stage.setTitle(AppContext.getString("app-title"));
		//ViewStack.reload();
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