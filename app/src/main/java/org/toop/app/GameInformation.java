package org.toop.app;

public class GameInformation {
	public enum Type {
		TICTACTOE,
		REVERSI;

		public static int playerCount(Type type) {
			return switch (type) {
				case TICTACTOE -> 2;
				case REVERSI -> 2;
			};
		}

		public static int maxDepth(Type type) {
			return switch (type) {
				case TICTACTOE -> 5;
				case REVERSI -> 0; // Todo
			};
		}
	}

	public static class Player {
		public String name = "";
		public boolean isHuman = true;
		public int computerDifficulty = 0;
		public int computerThinkTime = 1;
	}

	public final Type type;
	public final Player[] players;

	public GameInformation(Type type) {
		this.type = type;
		players = new Player[Type.playerCount(type)];

		for (int i = 0; i < players.length; i++) {
			players[i] = new Player();
		}
	}
}