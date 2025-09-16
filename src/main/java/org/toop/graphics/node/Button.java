package org.toop.graphics.node;

import org.toop.core.*;
import org.toop.math.*;

public class Button extends Node {
	ICallable<Boolean> onHover;
	ICallable<Boolean> onClick;

	public Button(int x, int y, int width, int height, Color color, ICallable<Boolean> onHover, ICallable<Boolean> onClick) {
		super(x, y, width, height, color);

		this.onHover = onHover;
		this.onClick = onClick;
	}

	@Override
	public void hover() {
		onHover.call();
	}

	@Override
	public void click() {
		onClick.call();
	}
}
