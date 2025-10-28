package org.toop.app;

public class GameInformation {
	public enum Type {
		TICTACTOE,
		REVERSI,
        CONNECT4,
        BATTLESHIP;


		public static int playerCount(Type type) {
			return switch (type) {
				case TICTACTOE -> 2;
				case REVERSI -> 2;
                case CONNECT4 -> 2;
                case BATTLESHIP -> 2;
			};
		}

		public static int maxDepth(Type type) {
			return switch (type) {
				case TICTACTOE -> 5; // Todo. 5 seems to always draw or win. could increase to 9 but that might affect performance
				case REVERSI -> 10; // Todo. 10 is a guess. might be too slow or too bad.
                case CONNECT4 -> 7;
                case BATTLESHIP -> 5;
			};
		}
	}

	public static class Player {
		public String name = "";
		public boolean isHuman = true;
		public int computerDifficulty = 1;
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