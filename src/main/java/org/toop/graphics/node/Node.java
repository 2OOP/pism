package org.toop.graphics.node;

import org.toop.math.*;

public abstract class Node {
	protected Bounds bounds;
	protected Color color;

	public Node(int x, int y, int width, int height, Color color) {
		bounds = new Bounds(x, y, width, height);
		this.color = color;
	}

	public boolean check(int x, int y) {
		return bounds.check(x, y);
	}

	public void hover() {}
	public void click() {}
}
