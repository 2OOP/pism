package org.toop.UI;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

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

    private String gameConnectionId;
    private String serverId;
    private String gameId;

    private LocalGameSelector parentSelector;

    public UIGameBoard(String gameType, String gameConnectionId, String serverId, String gameId, LocalGameSelector parent) {
        this.parentSelector = parent;

        this .gameConnectionId = gameConnectionId;
        this.serverId = serverId;
        this.gameId = gameId;

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
            cells[i].setFont(new Font("Arial", Font.BOLD, 100 / sizeX));
            panel.add(cells[i]);

            final int index = i;
            cells[i].addActionListener((ActionEvent e) -> {
                cells[index].setText(currentPlayer);
                if (Objects.equals(currentPlayer, "X")) { currentPlayer = "O"; }
                else { currentPlayer = "X"; }
                GlobalEventBus.post(new Events.ServerEvents.SendCommand(this.gameConnectionId,
                        "gameid ", this.gameId,
                        "player ", this.currentPlayer, // TODO: Actual player names
                        "MOVE", "" + index));
                System.out.println("Cell clicked: " + index);
            });
        }

        return panel;
    }

    public JPanel getTTTPanel() {
        return tttPanel;
    }
}