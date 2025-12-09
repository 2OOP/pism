package org.toop.app;

public class GameInformation {
	public enum Type {
		TICTACTOE(2, 5),
		REVERSI(2, 10);

        private final int playerCount;
        private final int maxDepth;

        Type(int playerCount, int maxDepth) {
            this.playerCount = playerCount;
            this.maxDepth = maxDepth;
        }

        public int getPlayerCount() {
            return playerCount;
        }

        public int getMaxDepth() {
            return maxDepth;
        }

        public String getTypeToString() {
            String name = this.name();
            return switch (name) {
                case "TICTACTOE" -> "TicTacToe";
                case "REVERSI" -> "Reversi";
                case "CONNECT4" -> "Connect4";
                case "BATTLESHIP" -> "Battleship";
                default -> name;
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
		players = new Player[type.getPlayerCount()];

		for (int i = 0; i < players.length; i++) {
			players[i] = new Player();
		}
	}
}