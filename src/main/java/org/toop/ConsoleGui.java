package org.toop;

import org.toop.game.*;
import org.toop.game.tictactoe.*;

import java.util.*;

public class ConsoleGui {
	private Scanner scanner;

	private TicTacToe game;
	private MinMaxTicTacToe ai;

	String ai1 = null;
	String ai2 = null;

	public ConsoleGui() {
		scanner = new Scanner(System.in);
		Random random = new Random(3453498);

		int mode = -1;

		System.out.print(
                """
                        1. player vs player
                        2. player vs ai
                        3. ai vs player
                        4. ai v ai
                        Choose mode (default is 1):\s""");
		String modeString = scanner.nextLine();

		try {
			mode = Integer.parseInt(modeString);
		} catch (Exception e) {
		}

		String player1 = null;
		String player2 = null;

		switch (mode) {
			// player vs ai
			case 2: {
				System.out.print("Please enter your name: ");
				String name = scanner.nextLine();

				player1 = name;
				ai2 = player2 = "AI #" + random.nextInt();

				break;
			}

			// ai vs player
			case 3: {
				System.out.print("Enter your name: ");
				String name = scanner.nextLine();

				ai1 = player1 = "AI #" + random.nextInt();
				player2 = name;

				break;
			}

			// ai vs ai
			case 4: {
				ai1 = player1 = "AI #" + random.nextInt();
				ai2 = player2 = "AI 2" + random.nextInt();

				break;
			}

			// player vs player
			case 1:
			default: {
				 System.out.print("Player 1. Please enter your name: ");
				 String name1 = scanner.nextLine();

				 System.out.print("Player 2. Please enter your name: ");
				 String name2 = scanner.nextLine();

				 player1 = name1;
				 player2 = name2;
			}
		}

		game = new TicTacToe(player1, player2);
		ai = new MinMaxTicTacToe();
	}

	public void print() {
		char[] seperator = new char[game.getSize() * 4 - 1];
		Arrays.fill(seperator, '-');

		for (int i = 0; i < game.getSize(); i++) {
			String buffer = " ";

			for (int j = 0; j < game.getSize() - 1; j++) {
				buffer += game.getGrid()[i * game.getSize() + j] + " | ";
			}

			buffer += game.getGrid()[i * game.getSize() + game.getSize() - 1];
			System.out.println(buffer);

			if (i < game.getSize() - 1) {
				System.out.println(seperator);
			}
		}
	}

	public boolean next() {
		Player current = game.getCurrentPlayer();
		int move = -1;

		if (ai1 != null && current.name() == ai1 || ai2 != null && current.name() == ai2) {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {}

			move = ai.findBestMove(game);
		} else {
			System.out.printf("%s's (%c) turn. Please choose an empty cell between 0-8: ", current.name(), current.move());
			String input = scanner.nextLine();

			try {
				move = Integer.parseInt(input);
			}
			catch (NumberFormatException e) {
			}
		}

		GameBase.State state = game.play(move);

		switch (state) {
			case INVALID: {
				System.out.println("Please select an empty cell. Between 0-8");
				return true;
			}

			case DRAW: {
				System.out.println("Game ended in a draw.");
				return false;
			}

			case WIN: {
				System.out.printf("%s has won the game.\n", game.getCurrentPlayer().name());
				return false;
			}

			case NORMAL:
			default: return true;
		} 
	}

	public GameBase getGame() { return game; }
}
