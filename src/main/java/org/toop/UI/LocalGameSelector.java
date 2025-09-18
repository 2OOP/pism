package org.toop.UI;

import jdk.jfr.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.Main;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class LocalGameSelector extends JFrame {
    private static final Logger logger = LogManager.getLogger(LocalGameSelector.class);

    private JPanel panel1;
    private JComboBox gameSelectionComboBox;
    private JButton startGame;
    private JComboBox playerTypeSelectionBox;

    private JPanel cards;        // CardLayout panel
    private CardLayout cardLayout;

    private UIGameBoard tttBoard;

    public LocalGameSelector() {
        setTitle("Local Game Selector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        // Setup CardLayout
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        setContentPane(cards);

        // --- Main menu panel ---
        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        gameSelectionComboBox = new JComboBox<>();
        gameSelectionComboBox.addItem("Tic Tac Toe");
        gameSelectionComboBox.addItem("Reversi");

        playerTypeSelectionBox = new JComboBox<>();
        playerTypeSelectionBox.addItem("Player vs Player");
        playerTypeSelectionBox.addItem("Player vs AI");
        playerTypeSelectionBox.addItem("AI vs Player");

        panel1.add(gameSelectionComboBox);
        panel1.add(playerTypeSelectionBox);

        startGame = new JButton("Start Game");
        panel1.add(startGame);

        cards.add(panel1, "MainMenu");

        // Start button action
        startGame.addActionListener(e -> startGameClicked());

        setVisible(true);
    }

    private String createServer() {
        CompletableFuture<String> serverIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartServerRequest("5001", "tictactoe", serverIdFuture)); // TODO: what if 5001 is in use
        try {
            return serverIdFuture.get();
        } catch (Exception e) {
            logger.error("Error getting server ID", e);
        }
        return null;
    }

    private String createConnection() {
        CompletableFuture<String> connectionIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest("127.0.0.1", "5001", connectionIdFuture)); // TODO: what if server couldn't be started with port.
        try {
            return connectionIdFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting connection ID", e);
        }
        return null;
    }

    private void startGameClicked() {
        String playerTypes = (String) playerTypeSelectionBox.getSelectedItem();
        String selectedGame = (String) gameSelectionComboBox.getSelectedItem();

        String serverId = createServer();
        String connectionId = createConnection();
        final String[] gameId = new String[1];

        if ("Player vs AI".equalsIgnoreCase(playerTypes)) {
            GlobalEventBus.post(new Events.ServerEvents.SendCommand(connectionId, "create_game", "Player", "AI"));
        } else if ("AI vs Player".equalsIgnoreCase(playerTypes)) {
            GlobalEventBus.post(new Events.ServerEvents.SendCommand(connectionId, "create_game", "Player", "AI"));
        } else { // Player vs Player is default
            GlobalEventBus.post(new Events.ServerEvents.SendCommand(connectionId, "create_game", "Player1", "Player2"));
        }

        CountDownLatch latch = new CountDownLatch(1); // TODO: This is bad, fix later

        new Thread(() -> {
            GlobalEventBus.subscribeAndRegister(Events.ServerEvents.ReceivedMessage.class, event -> {
                logger.info(event.message());
                if (event.message().toLowerCase().startsWith("game created successfully")) {
                    String[] parts = event.message().split("\\|");
                    String gameIdPart = parts[1];
                    gameId[0] = gameIdPart.split(" ")[1];
                    latch.countDown();
                }
            });
        }).start();

        try {
            latch.await(); // TODO: Bad, fix later

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        GlobalEventBus.post(new Events.ServerEvents.SendCommand(connectionId, "START_GAME", gameId[0]));

        if ("Tic Tac Toe".equalsIgnoreCase(selectedGame)) {
            if (tttBoard == null) {
                tttBoard = new UIGameBoard("tic tac toe", connectionId, serverId, gameId[0], this);
                cards.add(tttBoard.getTTTPanel(), "TicTacToe");
            }
            cardLayout.show(cards, "TicTacToe");
        }
    }

    public void showMainMenu() {
        cardLayout.show(cards, "MainMenu");
    }
}
