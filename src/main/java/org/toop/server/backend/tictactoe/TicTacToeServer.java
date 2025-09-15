package org.toop.server.backend.tictactoe;

import org.toop.server.backend.tictactoe.game.TicTacToe;
import org.toop.server.backend.TcpServer;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.net.Socket;
import java.util.ArrayList;

public class TicTacToeServer extends TcpServer {

    private TicTacToe game;

    public TicTacToeServer(int port, String playerA, String playerB) throws IOException {
        super(port);
        this.game = new TicTacToe(playerA, playerB);
    }

    @Override
    public void run() {
        try {
            logger.info("Tic tac toe server listening on port {}", this.port);

            while (isRunning()) {
                Socket clientSocket = this.serverSocket.accept();
                logger.info("Connected to client: {}", clientSocket.getInetAddress());

                new Thread(() -> this.startWorkers(clientSocket)).start();
                new Thread(() -> this.gameThread()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface TicTacToeCommand {
        Object execute(TicTacToe game, String[] args);
    }

    private TicTacToeCommand parseCommand(String command) {
        if (command.isEmpty()) {
            try {
                wait(250); // TODO: Magic number
                return null;
            } catch (InterruptedException e) {
                logger.error("Interrupted", e);
                throw new RuntimeException(e); // TODO: Maybe not throw it.
            }
        }

        String[] segments = command.split(" ");
        if (segments[0].isEmpty()) return null;

        TicTacToeServerCommand commandEnum = TicTacToeServerCommand.getCommand(segments[0]);
        switch (commandEnum) {
            case MOVE -> {
                if (segments.length > 1 && !segments[1].isEmpty()) return null;
                try {
                    int parsedInteger = Integer.parseInt(segments[1]);
                    if (this.game.validateMove(parsedInteger)) return this.game.playMove(parsedInteger);
                };
            }
        }

    }

    private void gameThread() {


        while (true) {
            String command = getNewestCommand();
            command = this.parseCommand(command);
            if (command == null) { continue; }



            // TODO: Game
        }

    }

}
