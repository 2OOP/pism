package org.toop.tictactoe.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.app.gui.LocalGameSelector;
import org.toop.app.gui.RemoteGameSelector;
import org.toop.game.Game;
import org.toop.tictactoe.LocalTicTacToe;

public class UIGameBoard {
    private static final int TICTACTOE_SIZE = 3;

    private static final Logger logger = LogManager.getLogger(LocalGameSelector.class);

    private JPanel tttPanel; // Root panel for this game
    private JButton backToMainMenuButton;
    private JButton[] cells;
    private String currentPlayer = "X";
    private int currentPlayerIndex = 0;

    private Object parentSelector;
    private boolean parentLocal;
    private LocalTicTacToe localTicTacToe;

    private boolean gameOver = false;

    public UIGameBoard(LocalTicTacToe lttt, Object parent) {
        if (!(parent == null)) {
            if (parent instanceof LocalGameSelector) {
                parentLocal = true;
            } else if (parent instanceof RemoteGameSelector) {
                parentLocal = false;
            }
        }
        this.parentSelector = parent;
        this.localTicTacToe = lttt;
        lttt.setUIReference(this);

        // Root panel
        tttPanel = new JPanel(new BorderLayout());

        // Back button
        backToMainMenuButton = new JButton("Back to Main Menu");

        tttPanel.add(backToMainMenuButton, BorderLayout.SOUTH);
        backToMainMenuButton.addActionListener(
                _ -> {
                    // TODO reset game and connections
                    // Game now gets reset in local
                    if (parentLocal) {
                        ((LocalGameSelector) parent).showMainMenu();
                    } else {
                        ((RemoteGameSelector) parent).showMainMenu();
                    }
                });

        // Game grid
        JPanel gameGrid = createGridPanel(TICTACTOE_SIZE, TICTACTOE_SIZE);
        tttPanel.add(gameGrid, BorderLayout.CENTER);

        //        localTicTacToe.setMoveListener((playerIndex, moveIndex, symbol) -> {
        //            SwingUtilities.invokeLater(() -> {
        //                cells[moveIndex].setText(String.valueOf(symbol));
        //            });
        //        });

    }

    private JPanel createGridPanel(int sizeX, int sizeY) {
        JPanel panel = new JPanel(new GridLayout(sizeX, sizeY));
        cells = new JButton[sizeX * sizeY];

        for (int i = 0; i < sizeX * sizeY; i++) {
            cells[i] = new JButton(" ");
            cells[i].setFont(new Font("Arial", Font.BOLD, 400 / sizeX));
            panel.add(cells[i]);
            cells[i].setFocusable(false);

            final int index = i;
            cells[i].addActionListener(
                    (ActionEvent _) -> {
                        if (!gameOver) {
                            if (cells[index].getText().equals(" ")) {
                                int cp = this.localTicTacToe.getCurrentPlayersTurn();
                                if (cp == 0) {
                                    this.currentPlayer = "X";
                                    currentPlayerIndex = 0;
                                } else if (cp == 1) {
                                    this.currentPlayer = "O";
                                    currentPlayerIndex = 1;
                                }
                                this.localTicTacToe.move(index);
                                cells[index].setText(currentPlayer);
                            } else {
                                logger.info(
                                        "Player "
                                                + currentPlayerIndex
                                                + " attempted invalid move at: "
                                                + cells[index].getText());
                            }
                        } else {
                            logger.info(
                                    "Player "
                                            + currentPlayerIndex
                                            + " attempted to move after the game has ended.");
                        }
                    });
        }

        return panel;
    }

    public void setCell(int index, String move) {
        System.out.println(cells[index].getText());
        cells[index].setText(move);
    }

    public void setState(Game.State state, String playerMove) {
        Color color;
        if (state == Game.State.WIN && playerMove.equals(currentPlayer)) {
            color = new Color(160, 220, 160);
        } else if (state == Game.State.WIN) {
            color = new Color(220, 160, 160);
        } else if (state == Game.State.DRAW) {
            color = new Color(220, 220, 160);
        } else {
            color = new Color(220, 220, 220);
        }
        for (JButton cell : cells) {
            cell.setBackground(color);
        }
        if (state == Game.State.DRAW || state == Game.State.WIN) {
            gameOver = true;
        }
    }

    public JPanel getTTTPanel() {
        return tttPanel;
    }
}
