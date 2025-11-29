package org.toop.app.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import java.util.Stack;

public final class ViewStack {
	private static boolean setup = false;

	private static StackPane root;

	private static View active;
	private static Stack<View> stack;

	public static void setup(Scene scene) {
		assert scene != null;

		if (setup) {
			return;
		}

		root = new StackPane();

		active = null;
		stack = new Stack<View>();

		scene.setRoot(root);

		setup = true;
	}

	public static void cleanup() {
		assert setup;

		final var count = stack.size();

		for (int i = 0; i < count; i++) {
			pop();
		}

		if (active != null) {
			active.cleanup();
		}

		setup = false;
	}

	public static void reload() {
		assert setup;

		for (final var view : stack) {
			view.cleanup();
		}

		if (active != null) {
			active.cleanup();
			active.setup();
		}

		for (final var view : stack) {
			view.setup();
		}
	}

	public static void push(View view) {
		assert setup;
		assert view != null;

		if (view.isMainView()) {
			Platform.runLater(() -> {
				if (active != null) {
					root.getChildren().removeFirst();
					active.cleanup();
				}

				root.getChildren().addFirst(view.getView());
				view.setup();

				active = view;
			});
		} else {
			Platform.runLater(() -> {
				stack.push(view);
				root.getChildren().addLast(view.getView());
				view.setup();
			});
		}
	}

	public static void pop() {
		assert setup;

		if (stack.isEmpty()) {
			return;
		}

		Platform.runLater(() -> {
			final var last = stack.pop();
			root.getChildren().removeLast();
			last.cleanup();
		});
	}
}