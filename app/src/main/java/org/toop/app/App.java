package org.toop.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.LoadingWidget;
import org.toop.app.widget.display.SongDisplay;
import org.toop.app.widget.popup.EscapePopup;
import org.toop.app.widget.popup.QuitPopup;
import org.toop.app.widget.view.MainView;
import org.toop.framework.audio.*;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.eventbus.GlobalEventBus;
import org.toop.framework.game.BitboardGame;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.connection.NetworkingClientEventListener;
import org.toop.framework.networking.connection.NetworkingClientManager;
import org.toop.framework.networking.server.GameDefinition;
import org.toop.framework.networking.server.MasterServer;
import org.toop.framework.resource.ResourceLoader;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.events.AssetLoaderEvents;
import org.toop.framework.resource.resources.CssAsset;
import org.toop.framework.resource.resources.MusicAsset;
import org.toop.framework.resource.resources.SoundEffectAsset;
import org.toop.local.AppContext;
import org.toop.local.AppSettings;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class App extends Application {
	private static Stage stage;
	private static Scene scene;

    private static int height;
    private static int width;

	public static void run(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
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

        stage.setMinWidth(1200);
        stage.setMinHeight(800);
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

		AppSettings.applySettings();

        setKeybinds(root);

        LoadingWidget loading = new LoadingWidget(Primitive.text(
                "Loading...", false), 0, 0, Integer.MAX_VALUE, false, false // Just set a high default
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

					Platform.runLater(() -> {
                        loading.setMaxAmount(e.isLoadingAmount());
						try {
							loading.setAmount(e.hasLoadedAmount());
						} catch (Exception ex) {
							throw new RuntimeException(ex);
						}
                        if (e.hasLoadedAmount() >= e.isLoadingAmount()-1) {
                            Platform.runLater(loading::triggerSuccess);
                            loadingFlow.unsubscribe("init_loading");
                        }
					});


                }, false, "init_loading");

		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.submit(
				() -> ResourceManager.loadAssets(new ResourceLoader("app/src/main/resources/assets")
			));
		} finally {
			executor.shutdown();
		}

		stage.show();
    }

	private void setKeybinds(StackPane root) {
		root.addEventHandler(KeyEvent.KEY_PRESSED,event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				escapePopup();	
			}
		});
        stage.setFullScreenExitKeyCombination(
                new KeyCodeCombination(
                        KeyCode.F11
                )
        );
	}

	public void escapePopup() {

		if (   WidgetContainer.getCurrentView() == null
			|| WidgetContainer.getCurrentView() instanceof MainView) {
			return;
		}

		if (!Objects.requireNonNull(
				WidgetContainer.find(widget -> widget instanceof QuitPopup || widget instanceof EscapePopup)
		).isEmpty()) {
			WidgetContainer.removeFirst(QuitPopup.class);
			WidgetContainer.removeFirst(EscapePopup.class);
			return;
		}

		EscapePopup escPopup = new EscapePopup();
		escPopup.show(Pos.CENTER);
	}

	private void setOnLoadingSuccess(LoadingWidget loading) {
		loading.setOnSuccess(() -> {

            try {
                initSystems();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            AppSettings.applyMusicVolumeSettings();
			new EventFlow().addPostEvent(new AudioEvents.StartBackgroundMusic()).postEvent();
            loading.hide();
			WidgetContainer.add(Pos.CENTER, new MainView());
			WidgetContainer.add(Pos.BOTTOM_RIGHT, new SongDisplay());
			stage.setOnCloseRequest(event -> {
				event.consume();

				if (WidgetContainer.getAllWidgets().stream().anyMatch(e -> e instanceof QuitPopup)) return;

				QuitPopup a = new QuitPopup();
				a.show(Pos.CENTER);

			});
		});
	}

	private void initSystems() throws InterruptedException { // TODO Move to better place

		final int THREAD_COUNT = 2;
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

		@SuppressWarnings("resource")
		ExecutorService threads = Executors.newFixedThreadPool(THREAD_COUNT);

		try {

			threads.submit(() -> {
				new NetworkingClientEventListener(
						GlobalEventBus.get(),
						new NetworkingClientManager(GlobalEventBus.get()));

				latch.countDown();
			});

			threads.submit(() -> {
				MusicManager<MusicAsset> musicManager =
						new MusicManager<>(
								GlobalEventBus.get(),
								ResourceManager.getAllOfTypeAndRemoveWrapper(MusicAsset.class),
								true
						);

				SoundEffectManager<SoundEffectAsset> soundEffectManager =
						new SoundEffectManager<>(ResourceManager.getAllOfType(SoundEffectAsset.class));

				AudioVolumeManager audioVolumeManager = new AudioVolumeManager()
						.registerManager(VolumeControl.MASTERVOLUME, musicManager)
						.registerManager(VolumeControl.MASTERVOLUME, soundEffectManager)
						.registerManager(VolumeControl.FX, soundEffectManager)
						.registerManager(VolumeControl.MUSIC, musicManager);

				new AudioEventListener<>(
						GlobalEventBus.get(),
						musicManager,
						soundEffectManager,
						audioVolumeManager
				).initListeners("medium-button-click.wav");

				latch.countDown();
			});

		} finally {
			latch.await();
			threads.shutdown();
		}
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