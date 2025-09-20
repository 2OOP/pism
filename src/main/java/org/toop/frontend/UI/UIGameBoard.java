package org.toop.frontend.UI;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.frontend.games.LocalTicTacToe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class UIGameBoard {
    private static final int TICTACTOE_SIZE = 3;

    private JPanel tttPanel;                  // Root panel for this game
    private JButton backToMainMenuButton;
    private JButton[] cells;
    private String currentPlayer = "X";
    private int currentPlayerIndex = 0;

    private LocalGameSelector parentSelector;
    private LocalTicTacToe localTicTacToe;

    public UIGameBoard(LocalTicTacToe lttt, LocalGameSelector parent) {
        this.parentSelector = parent;
        this.localTicTacToe = lttt;
        lttt.setUIReference(this);

        // Root panel
        tttPanel = new JPanel(new BorderLayout());

        // Back button
        backToMainMenuButton = new JButton("Back to Main Menu");
        tttPanel.add(backToMainMenuButton, BorderLayout.SOUTH);
        backToMainMenuButton.addActionListener(e ->
                // TODO reset game and connections
                parent.showMainMenu()
        );

        // Game grid
        JPanel gameGrid = createGridPanel(TICTACTOE_SIZE, TICTACTOE_SIZE);
        tttPanel.add(gameGrid, BorderLayout.CENTER);
    }

    private JPanel createGridPanel(int sizeX, int sizeY) {
        JPanel panel = new JPanel(new GridLayout(sizeX, sizeY));
        cells = new JButton[sizeX * sizeY];

        for (int i = 0; i < sizeX * sizeY; i++) {
            cells[i] = new JButton(" ");
            cells[i].setFont(new Font("Arial", Font.BOLD, 400 / sizeX));
            panel.add(cells[i]);

            final int index = i;
            cells[i].addActionListener((ActionEvent e) -> {
                int cp = this.localTicTacToe.getCurrentPlayersTurn();
                if      (cp == 0) { this.currentPlayer = "X"; currentPlayerIndex = 0; }
                else if (cp == 1) { this.currentPlayer = "O"; currentPlayerIndex = 1; }
                this.localTicTacToe.move(index);
                cells[index].setText(currentPlayer);
            });
        }

        return panel;
    }
    public void setCell(int index, String move){
        System.out.println(cells[index].getText());
        cells[index].setText(move);
    }

    public JPanel getTTTPanel() {
        return tttPanel;
    }
}