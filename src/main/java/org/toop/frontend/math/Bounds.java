package org.toop.frontend.math;

public class Bounds {
	private int x;
	private int y;
	private int width;
	private int height;

	public Bounds(int x, int y, int width, int height) {
		set(x, y, width, height);
	}

	public void set(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }

	public boolean check(int x, int y) {
		return
			x >= this.x && x <= this.x + this.width &&
			y >= this.y && y <= this.y + this.height;
	}
}
