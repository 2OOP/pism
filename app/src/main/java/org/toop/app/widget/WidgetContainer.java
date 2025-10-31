package org.toop.app.widget;

import org.toop.app.widget.complex.PopupWidget;
import org.toop.app.widget.complex.PrimaryWidget;

import java.util.ArrayDeque;
import java.util.Deque;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public final class WidgetContainer {
	private static final Deque<PopupWidget> popups = new ArrayDeque<>();

	private static StackPane root;

	public static synchronized StackPane setup() {
		if (root != null) {
			return root;
		}

		root = new StackPane();
		root.getStyleClass().add("bg-primary");

		return root;
	}

	public static void add(Pos position, Widget widget) {
		if (root == null || widget == null) {
			return;
		}

		Platform.runLater(() -> {
			if (root.getChildren().contains(widget.getNode())) {
				return;
			}

			StackPane.setAlignment(widget.getNode(), position);

			if (widget instanceof PrimaryWidget) {
				root.getChildren().addFirst(widget.getNode());
			} else {
				root.getChildren().add(widget.getNode());
			}

			if (widget instanceof PopupWidget popup) {
				popups.push(popup);
			}
		});
	}

	public static void remove(Widget widget) {
		if (root == null || widget == null) {
			return;
		}

		Platform.runLater(() -> {
			root.getChildren().remove(widget.getNode());

			if (widget instanceof PrimaryWidget) {
				for (var popup : popups) {
					root.getChildren().remove(popup.getNode());
				}

				popups.clear();
			}
		});
	}
}