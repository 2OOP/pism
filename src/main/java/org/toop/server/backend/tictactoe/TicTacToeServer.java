package org.toop.server.backend.tictactoe;

import org.toop.server.backend.TcpServer;
import org.toop.game.TTT;

import java.io.IOException;

public class TicTacToeServer extends TcpServer {
    public TicTacToeServer(int port) throws IOException {
        super(port);
    }

    private void gameThread(String main_character, String opponent_character) {
        TTT ticTacToe = new TTT(main_character, opponent_character);
    }

}
