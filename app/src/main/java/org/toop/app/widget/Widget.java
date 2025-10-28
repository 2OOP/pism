package org.toop.app.widget;

import javafx.geometry.Pos;
import javafx.scene.Node;

public interface Widget<T extends Node> {
	T getNode();

    default void show(Pos position) {
        WidgetContainer.add(position, this);
    }

    default void hide() {
        WidgetContainer.remove(this);
    }

    default void replace(Widget<?> newWidget, Pos newWidgetPosition) {
        this.hide();
        newWidget.show(newWidgetPosition);
    }
}