package org.toop.frontend.UI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.frontend.games.LocalTicTacToe;

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

    private void startGameClicked() {
        String playerTypes = (String) playerTypeSelectionBox.getSelectedItem();
        String selectedGame = (String) gameSelectionComboBox.getSelectedItem();

        LocalTicTacToe lttt = new LocalTicTacToe(true, "127.0.0.1", "5001");

        if ("Tic Tac Toe".equalsIgnoreCase(selectedGame)) {
            if (tttBoard == null) {
                tttBoard = new UIGameBoard(lttt, this);
                cards.add(tttBoard.getTTTPanel(), "TicTacToe");
            }
            cardLayout.show(cards, "TicTacToe");
        }
    }

    public void showMainMenu() {
        cardLayout.show(cards, "MainMenu");
    }
}
