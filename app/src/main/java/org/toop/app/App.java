package org.toop.app;

import org.toop.app.menu.MainMenu;
import org.toop.app.menu.Menu;
import org.toop.app.menu.QuitMenu;

import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	private static Stage stage;
	private static Scene scene;
	private static StackPane root;

	public static void run(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		final StackPane root = new StackPane(new MainMenu().getPane());
		final Scene scene = new Scene(root);

		stage.setTitle("pism");
		stage.setMinWidth(1080);
		stage.setMinHeight(720);

		stage.setOnCloseRequest(event -> {
			event.consume();
			push(new QuitMenu());
		});

		stage.setScene(scene);
		stage.setResizable(false);

		stage.show();

		App.stage = stage;
		App.scene = scene;
		App.root = root;
	}

	public static void activate(Menu menu) {
		scene.setRoot(menu.getPane());
	}

	public static void push(Menu menu) {
		root.getChildren().add(menu.getPane());
	}

	public static void pop() {
		root.getChildren().removeLast();
	}

	public static void quit() {
		stage.close();
	}

	public static StackPane getRoot() { return root; }
}