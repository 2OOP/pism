package org.toop.app.layer.containers;

import org.toop.app.layer.Container;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public final class HorizontalContainer extends Container {
	private final HBox container;

	public HorizontalContainer(String cssClass, int spacing) {
		container = new HBox(spacing);
		container.getStyleClass().add(cssClass);
	}

	public HorizontalContainer(int spacing) {
		this("horizontal_container", spacing);
	}

	@Override
	public Region getContainer() {
		return container;
	}

	@Override
	public void addNode(Node node) {
		container.getChildren().addLast(node);
	}

	@Override
	public void addContainer(Container container, boolean fill) {
		if (fill) {
			container.getContainer().setMinSize(0, 0);
			container.getContainer().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			HBox.setHgrow(container.getContainer(), Priority.ALWAYS);
		} else {
			container.getContainer().setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		}

		this.container.getChildren().add(container.getContainer());

		if (fill) {
			balanceChildWidths();
		}
	}

	private void balanceChildWidths() {
		final ObservableList<Node> children = container.getChildren();
		final double widthPerChild = container.getWidth() / children.size();

		for (final Node child : children) {
			if (child instanceof Region) {
				((Region) child).setPrefWidth(widthPerChild);
			}
		}
	}
}