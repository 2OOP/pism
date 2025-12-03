package org.toop.app;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.LoadingWidget;
import org.toop.app.widget.display.SongDisplay;
import org.toop.app.widget.popup.QuitPopup;
import org.toop.app.widget.view.MainView;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.resource.ResourceLoader;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.events.AssetLoaderEvents;
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

		scene.getRoot();

        stage.setMinWidth(1080);
        stage.setMinHeight(720);
		stage.setOnCloseRequest(event -> {
			event.consume();
			quit();
		});

		stage.setScene(scene);
		stage.setResizable(true);

		stage.show();

		App.stage = stage;
		App.scene = scene;

		App.width = (int)stage.getWidth();
		App.height = (int)stage.getHeight();

		App.isQuitting = false;

        var loading = new LoadingWidget(Primitive.text(
                "Loading...", false), 0, 0, 9999
        );

        WidgetContainer.add(Pos.CENTER, loading);

        loading.setOnSuccess(() -> {
            AppSettings.applySettings();
            loading.hide();
            WidgetContainer.add(Pos.CENTER, new MainView());
            WidgetContainer.add(Pos.BOTTOM_RIGHT, new SongDisplay());
            stage.setOnCloseRequest(event -> {
                event.consume();
                startQuit();
            });
        });

        var loadingFlow = new EventFlow();
        loadingFlow
                .listen(AssetLoaderEvents.LoadingProgressUpdate.class, e -> {

                    loading.setMaxAmount(e.isLoadingAmount()-1);

                    try {
                        loading.setAmount(e.hasLoadedAmount());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (e.hasLoadedAmount() >= e.isLoadingAmount()-1) {
                        loading.triggerSuccess();
                        loadingFlow.unsubscribe("initloading");
                    }

                }, false, "initloading");

        ResourceManager.loadAssets(new ResourceLoader("app/src/main/resources/assets"));
        new EventFlow().addPostEvent(new AudioEvents.StartBackgroundMusic()).asyncPostEvent();
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