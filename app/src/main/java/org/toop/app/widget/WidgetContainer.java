package org.toop.app.widget;

import javafx.scene.Node;
import org.toop.app.widget.complex.PopupWidget;
import org.toop.app.widget.complex.ViewWidget;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class WidgetContainer {
	private static StackPane root;
	private static ViewWidget currentView;

	public static synchronized StackPane setup() {
		if (root != null) {
			return root;
		}

		root = new StackPane();
		root.getStyleClass().add("bg-view");

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

			if (widget instanceof ViewWidget view) {
				root.getChildren().addFirst(view.getNode());
				currentView = view;
			} else if (widget instanceof PopupWidget popup) {
				currentView.add(Pos.CENTER, (Widget) popup);
			} else {
				root.getChildren().add(widget.getNode());
			}
		});
	}

	public static void remove(Widget widget) {
		if (root == null || widget == null) {
			return;
		}

		Platform.runLater(() -> {
			if (widget instanceof PopupWidget popup) {
				currentView.remove((Widget) popup);
			} else {
				root.getChildren().remove(widget.getNode());
			}
		});
	}

	public static void remove(Class<? extends Widget> widgetClass) {
		if (root == null || currentView == null) return;

		Platform.runLater(() ->
				currentView.getChildren().removeIf(widget -> widget.getClass().isAssignableFrom(widgetClass))
		);
	}

	public static void removeFirst(Class<? extends Widget> widgetClass) {
		if (root == null || currentView == null) return;

		Platform.runLater(() -> {
			for (Node widget : currentView.getChildren()) {
				if (widgetClass.isAssignableFrom(widget.getClass())) {
					currentView.getChildren().remove(widget);
					break;
				}
			}
		});
	}

	public static List<Widget> find(Class<? extends Widget> widgetClass) {
		if (root == null || currentView == null) return null;

		return getAllWidgets()
				.stream()
				.filter(widget -> widget.getClass().isAssignableFrom(widgetClass))
				.toList();
    }

	public static List<Widget> find(Predicate<Widget> predicate) {
		if (root == null || currentView == null) return null;

		return getAllWidgets()
				.stream()
				.filter(predicate)
				.toList();
	}

	public static Widget findFirst(Class<? extends Widget> widgetClass) {
		if (root == null || currentView == null) return null;

		return getAllWidgets()
				.stream()
				.filter(widget -> widget.getClass().isAssignableFrom(widgetClass))
				.findFirst().orElse(null);
	}

	public static ViewWidget getCurrentView() {
		return currentView;
	}

    public static void setCurrentView(ViewWidget view) {
        if (root == null || view == null) {
            return;
        }

        Platform.runLater(() -> {
            root.getChildren().clear();
            root.getChildren().add(view.getNode());
            currentView = view;
        });
    }

	public static List<Widget> getAllWidgets() {
		final List<Widget> children = new ArrayList<>();

		for (var child : root.getChildren()) {
			if (child instanceof Widget widget) {
				children.add(widget);
			}
		}

		for (var child : currentView.getNode().getChildren()) {
			if (child instanceof Widget widget) {
				children.add(widget);
			}
		}

		return children;
	}
}