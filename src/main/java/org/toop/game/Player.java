package org.toop.game;

public class Player {
	private String name;
	private char move;

	public Player(String name, char move) {
		this.name = name;
		this.move = move;
	}

	public String Name() {
		return name;
	}

	public char Move() {
		return move;
	}
}
