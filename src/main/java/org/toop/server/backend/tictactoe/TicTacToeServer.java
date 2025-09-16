package org.toop.server.backend.tictactoe;

import org.toop.server.backend.tictactoe.game.TicTacToe;
import org.toop.server.backend.TcpServer;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private static class ParsedCommand {
        public TicTacToeServerCommand command;
        public ArrayList<Object> arguments;
        public boolean isValidCommand;
        public TicTacToeServerMessage returnMessage;
        public String errorMessage;
        public String originalCommand;

        ParsedCommand(String receivedCommand) {

            if (receivedCommand.isEmpty()) {
                this.command = null;
                this.arguments = null;
                this.isValidCommand = false;
                this.returnMessage = TicTacToeServerMessage.ERR;
                this.errorMessage = "The received command is empty";
                this.originalCommand = receivedCommand;
                return;
            }

            String[] segments = receivedCommand.split(" ");
            if (segments[0].isEmpty()) {
                this.command = null;
                this.arguments = null;
                this.isValidCommand = false;
                this.returnMessage = TicTacToeServerMessage.ERR;
                this.errorMessage = "The received command is empty or couldn't be split";
                this.originalCommand = receivedCommand;
                return;
            };

            TicTacToeServerCommand commandEnum = TicTacToeServerCommand.getCommand(segments[0]);
            switch (commandEnum) {
                case MOVE -> {
                    if (segments.length == 2 && !segments[1].isEmpty()) {
                        this.command = commandEnum;
                        this.arguments = new ArrayList<Object>();
                        this.arguments.add(segments[1]);
                        this.returnMessage = TicTacToeServerMessage.OK;
                        this.isValidCommand = true;
                        this.errorMessage = null;
                        this.originalCommand = receivedCommand;
                        return;
                    }
                }
                case
            }

            this.command = command;
            this.arguments = arguments;
        }

    }

    private ParsedCommand parseCommand(String command) {

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
