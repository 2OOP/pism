package org.toop.app;

public enum GameType {
	TICTACTOE, REVERSI;

	public static String toName(GameType type) {
		return switch (type) {
			case TICTACTOE -> "Tic Tac Toe";
			case REVERSI -> "Reversi";
		};
	}

	public static GameType toType(String name) {
		return switch (name) {
			case "Tic Tac Toe" -> TICTACTOE;
			case "Reversi" -> REVERSI;

			default -> TICTACTOE;
		};
	}
}