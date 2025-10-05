package org.toop.app;

public enum GameType {
	TICTACTOE, OTHELLO;

	public static String toName(GameType type) {
		return switch (type) {
			case TICTACTOE -> "tic-tac-toe";
			case OTHELLO -> "Othello";
		};
	}

	public static GameType toType(String name) {
		return switch (name) {
			case "tic-tac-toe" -> TICTACTOE;
			case "Reversi" -> OTHELLO;

			default -> TICTACTOE;
		};
	}
}