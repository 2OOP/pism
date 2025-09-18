package org.toop.graphics.node;

import org.toop.math.*;

import java.util.*;

public class Widget {
	Bounds bounds;
	private ArrayList<Node> nodes;

	public Widget(Bounds bounds) {
		this.bounds = bounds;
		nodes = new ArrayList<Node>();
	}

	public boolean check(int x, int y) {
		return bounds.check(x, y);
	}

	public void add(Node node) {
		nodes.add(node);
	}

	public boolean hover(int x, int y) {
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);

			if (node.check(x, y)) {
				node.hover();
				return true;
			}
		}

		return false;
	}

	public boolean click(int x, int y) {
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);

			if (node.check(x, y)) {
				node.click();
				return true;
			}
		}

		return false;
	}
}
