package org.toop.app;

public enum GameType {
	TICTACTOE, OTHELLO;

	public static String toName(GameType type) {
		return switch (type) {
			case TICTACTOE -> "Tic Tac Toe";
			case OTHELLO -> "Othello";
		};
	}

	public static GameType toType(String name) {
		return switch (name) {
			case "Tic Tac Toe" -> TICTACTOE;
			case "Reversi" -> OTHELLO;

			default -> TICTACTOE;
		};
	}
}