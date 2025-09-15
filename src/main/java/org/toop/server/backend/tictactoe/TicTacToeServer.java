package org.toop.server.backend.tictactoe;

import org.toop.server.backend.tictactoe.game.TicTacToe;
import org.toop.server.backend.TcpServer;

import java.io.IOException;
import java.net.Socket;

public class TicTacToeServer extends TcpServer {
    public TicTacToeServer(int port) throws IOException {
        super(port);
    }

    @Override
    public void run() {
        try {
            logger.info("Server listening on port {}", port);

            while (running) {
                Socket clientSocket = this.serverSocket.accept();
                logger.info("Connected to client: {}", clientSocket.getInetAddress());

                new Thread(() -> this.startWorkers(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameThread(String main_character, String opponent_character) {
        TicTacToe ticTacToe = new TicTacToe(main_character, opponent_character);

        while (true) {
            // TODO: Game
        }

    }

}
