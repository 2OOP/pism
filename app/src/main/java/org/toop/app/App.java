package org.toop.app;

import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.menu.MainMenu;
import org.toop.app.menu.Menu;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.CssAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

public final class App extends Application {
	private static Stage stage;
	private static StackPane root;

	private static int width;
	private static int height;

	private static boolean isQuitting;

	private static class QuitMenu extends Menu {
		public QuitMenu() {
			final Region background = createBackground("quit_background");

			final Text sure = createText("Are you sure?");

			final Button yes = createButton("Yes", () -> { stage.close(); });
			final Button no = createButton("No", () -> { pop(); isQuitting = false; });

			final HBox buttons = new HBox(50, yes, no);
			buttons.setAlignment(Pos.CENTER);

			final VBox box = new VBox(35, sure, buttons);
			box.getStyleClass().add("quit_box");
			box.setAlignment(Pos.CENTER);
			box.setMaxWidth(350);
			box.setMaxHeight(200);

			pane = new StackPane(background, box);
			pane.getStylesheets().add(ResourceManager.get(CssAsset.class, "quit.css").getUrl());
		}
	}

	public static void run(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		final StackPane root = new StackPane(new MainMenu().getPane());

		final Scene scene = new Scene(root);
		scene.getStylesheets().add(((CssAsset)ResourceManager.get("app.css")).getUrl());

		stage.setTitle("pism");
		stage.setMinWidth(1080);
		stage.setMinHeight(720);

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

		new EventFlow().addPostEvent(new AudioEvents.StartBackgroundMusic()).asyncPostEvent();
		new EventFlow().addPostEvent(new AudioEvents.ChangeVolume(0.1)).asyncPostEvent();

		TicTacToeCanvas canvas = new TicTacToeCanvas();
		root.getChildren().addLast(canvas.getCanvas());
	}

	public static void quitPopup() {
		isQuitting = true;
		push(new QuitMenu());
	}

	public static void activate(Menu menu) {
		pop();
		push(menu);
	}

	public static void push(Menu menu) {
		root.getChildren().addLast(menu.getPane());
	}

	public static void pop() {
		root.getChildren().removeLast();
	}

	public static int getWidth() { return width; }
	public static int getHeight() { return height; }
}