package org.toop.app.gui;

import java.awt.*;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.app.tictactoe.LocalTicTacToe;
import org.toop.app.tictactoe.gui.UIGameBoard;

public class LocalGameSelector extends JFrame {
    private static final Logger logger = LogManager.getLogger(LocalGameSelector.class);

    private JPanel panel1;
    private JComboBox gameSelectionComboBox;
    private JButton startGame;
    private JComboBox playerTypeSelectionBox;
    private JButton deleteSave;

    private JPanel cards; // CardLayout panel
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

        deleteSave = new JButton("Delete Save");
        panel1.add(deleteSave);
        deleteSave.setEnabled(false);
        deleteSave.addActionListener(
                e -> {
                    tttBoard = null;
                    deleteSave.setEnabled(false);
                });

        cards.add(panel1, "MainMenu");

        // Start button action
        startGame.addActionListener(e -> startGameClicked());

        setVisible(true);
    }

    private void startGameClicked() {
        String playerTypes = (String) playerTypeSelectionBox.getSelectedItem();
        String selectedGame = (String) gameSelectionComboBox.getSelectedItem();

        LocalTicTacToe lttt = null;

        if (playerTypes.equals("Player vs Player")) {
            logger.info("Player vs Player");
            lttt = LocalTicTacToe.createLocal(new boolean[] {false, false});
        } else {
            if (playerTypes.equals("Player vs AI")) {
                logger.info("Player vs AI");
                lttt = LocalTicTacToe.createLocal(new boolean[] {false, true});
            } else {
                logger.info("AI vs Player");
                lttt = LocalTicTacToe.createLocal(new boolean[] {true, false});
            }
        }

        if ("Tic Tac Toe".equalsIgnoreCase(selectedGame)) {
            if (tttBoard == null) {
                tttBoard = new UIGameBoard(lttt, this);
                cards.add(tttBoard.getTTTPanel(), "TicTacToe");
            }
            cardLayout.show(cards, "TicTacToe");
        }
        lttt.startThreads();
    }

    public void showMainMenu() {
        cardLayout.show(cards, "MainMenu");
        gameSelectionComboBox.setSelectedIndex(0);
        playerTypeSelectionBox.setSelectedIndex(0);
        if (tttBoard != null) {
            deleteSave.setEnabled(true);
        }
    }
}
