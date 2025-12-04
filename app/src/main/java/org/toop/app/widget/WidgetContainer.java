package org.toop.app.widget;

import org.toop.app.widget.complex.PopupWidget;
import org.toop.app.widget.complex.ViewWidget;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

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
				currentView.add(Pos.CENTER, popup);
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
				currentView.remove(popup);
			} else {
				root.getChildren().remove(widget.getNode());
			}
		});
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

		return children;
	}
}