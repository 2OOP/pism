package org.toop.game;

// Todo: refactor
public class Player {
	String name;
	char symbol;

	Player(String name, char symbol) {
		this.name = name;
		this.symbol = symbol;
	}

	public String getName() {
		return this.name;
	}

	public char getSymbol() {
		return this.symbol;
	}
}