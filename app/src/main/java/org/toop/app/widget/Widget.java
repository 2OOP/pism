package org.toop.app.widget;

import javafx.geometry.Pos;
import javafx.scene.Node;

public interface Widget {
	Node getNode();

    default void show(Pos position) {
        WidgetContainer.add(position, this);
    }

    default void hide() {
        WidgetContainer.remove(this);
    }

    default void replace(Pos position, Widget widget) {
        widget.show(position);
		hide();
    }
}