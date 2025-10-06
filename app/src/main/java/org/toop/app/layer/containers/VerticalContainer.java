package org.toop.app.layer.containers;

import org.toop.app.layer.Container;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class VerticalContainer extends Container {
	private final VBox container;

	public VerticalContainer(int spacing, String... cssClasses) {
		container = new VBox(spacing);
		container.getStyleClass().addAll(cssClasses);
	}

	public VerticalContainer(int spacing) {
		this(spacing, "container");
	}

	@Override
	public Region getContainer() {
		return container;
	}

	@Override
	public void addNodes(Node... nodes) {
		container.getChildren().addAll(nodes);
	}

	@Override
	public void addContainer(Container container, boolean fill) {
		if (fill) {
			container.getContainer().setMinSize(0, 0);
			container.getContainer().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			VBox.setVgrow(container.getContainer(), Priority.ALWAYS);
		} else {
			container.getContainer().setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		}

		this.container.getChildren().add(container.getContainer());

		if (fill) {
			balanceChildHeights();
		}
	}

	private void balanceChildHeights() {
		final ObservableList<Node> children = container.getChildren();
		final double heightPerChild = container.getHeight() / children.size();

		for (final Node child : children) {
			if (child instanceof Region) {
				((Region) child).setPrefHeight(heightPerChild);
			}
		}
	}
}