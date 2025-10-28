package org.toop.app.widget;

import javafx.scene.Node;

public interface Widget<T extends Node> {
	T getNode();
}