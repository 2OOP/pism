package org.toop.app.layer;

import javafx.scene.Node;
import javafx.scene.layout.Region;

public abstract class Container {
	public abstract Region getContainer();

	public abstract void addNodes(Node... nodes);
	public abstract void addContainer(Container container, boolean fill);
}