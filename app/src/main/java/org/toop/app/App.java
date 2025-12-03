package org.toop.app;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.toop.Main;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.LoadingWidget;
import org.toop.app.widget.display.SongDisplay;
import org.toop.app.widget.popup.QuitPopup;
import org.toop.app.widget.view.MainView;
import org.toop.framework.audio.*;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.NetworkingClientEventListener;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.resource.ResourceLoader;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.events.AssetLoaderEvents;
import org.toop.framework.resource.resources.CssAsset;
import org.toop.framework.resource.resources.MusicAsset;
import org.toop.framework.resource.resources.SoundEffectAsset;
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
		// Start loading localization
		ResourceManager.loadAssets(new ResourceLoader("app/src/main/resources/localization"));
		ResourceManager.loadAssets(new ResourceLoader("app/src/main/resources/style"));

        final StackPane root = WidgetContainer.setup();
		final Scene scene = new Scene(root);

		stage.setOpacity(0.0);

		stage.setTitle(AppContext.getString("app-title"));
		stage.titleProperty().bind(AppContext.bindToKey("app-title"));

		stage.setWidth(0);
		stage.setHeight(0);

		scene.getRoot();

        stage.setMinWidth(1080);
        stage.setMinHeight(720);
		stage.setOnCloseRequest(event -> {
			event.consume();
			quit();
		});

		stage.setScene(scene);
		stage.setResizable(true);

		App.stage = stage;
		App.scene = scene;

		App.width = (int)stage.getWidth();
		App.height = (int)stage.getHeight();

		App.isQuitting = false;

		AppSettings.applySettings();

        LoadingWidget loading = new LoadingWidget(Primitive.text(
                "Loading...", false), 0, 0, Integer.MAX_VALUE, false // Just set a high default
        );

		WidgetContainer.setCurrentView(loading);

		setOnLoadingSuccess(loading);

        EventFlow loadingFlow = new EventFlow();

		final boolean[] hasRun = {false};
        loadingFlow
                .listen(AssetLoaderEvents.LoadingProgressUpdate.class, e -> {
					if (!hasRun[0]) {
						hasRun[0] = true;
						try {
							Thread.sleep(100);
						} catch (InterruptedException ex) {
							throw new RuntimeException(ex);
						}
						Platform.runLater(() -> stage.setOpacity(1.0));
					}

					Platform.runLater(() -> loading.setMaxAmount(e.isLoadingAmount()));

					Platform.runLater(() -> {
						try {
							loading.setAmount(e.hasLoadedAmount());
						} catch (Exception ex) {
							throw new RuntimeException(ex);
						}
					});

                    if (e.hasLoadedAmount() >= e.isLoadingAmount()) {
                        Platform.runLater(loading::triggerSuccess);
                        loadingFlow.unsubscribe("init_loading");
                    }

                }, false, "init_loading");

		// Start loading assets
		new Thread(() -> ResourceManager.loadAssets(new ResourceLoader("app/src/main/resources/assets")))
				.start();

		stage.show();

	}

	private void setOnLoadingSuccess(LoadingWidget loading) {
		loading.setOnSuccess(() -> {
			initSystems();
			AppSettings.applyMusicVolumeSettings();
			new EventFlow().addPostEvent(new AudioEvents.StartBackgroundMusic()).postEvent();
            loading.hide();
			WidgetContainer.add(Pos.CENTER, new MainView());
			WidgetContainer.add(Pos.BOTTOM_RIGHT, new SongDisplay());
			stage.setOnCloseRequest(event -> {
				event.consume();
				startQuit();
			});
		});
	}

	private void initSystems() { // TODO Move to better place
		new Thread(() -> new NetworkingClientEventListener(new NetworkingClientManager())).start();

		new Thread(() -> {
			MusicManager<MusicAsset> musicManager =
					new MusicManager<>(ResourceManager.getAllOfTypeAndRemoveWrapper(MusicAsset.class), true);

			SoundEffectManager<SoundEffectAsset> soundEffectManager =
					new SoundEffectManager<>(ResourceManager.getAllOfType(SoundEffectAsset.class));

			AudioVolumeManager audioVolumeManager = new AudioVolumeManager()
					.registerManager(VolumeControl.MASTERVOLUME, musicManager)
					.registerManager(VolumeControl.MASTERVOLUME, soundEffectManager)
					.registerManager(VolumeControl.FX, soundEffectManager)
					.registerManager(VolumeControl.MUSIC, musicManager);

			new AudioEventListener<>(
					musicManager,
					soundEffectManager,
					audioVolumeManager
			).initListeners("medium-button-click.wav");

		}).start();

		// Threads must be ready, before continue, TODO use latch instead
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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