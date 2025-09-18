package org.toop.UI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.Main;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UIGameBoard extends JFrame {
    private static final Logger logger = LogManager.getLogger(UIGameBoard.class);

    private final int TICTACTOE_SIZE = 3;
    private final int REVERSI_SIZE = 8;
    private JLabel name;
    private JLabel ip;
    private JLabel gameName;
    private JPanel tttPanel;
    private JPanel cellPanel;
    private JButton backToMainMenuButton;
    private JButton[] buttons = new JButton[9];
    private JButton[] cells;
    public UIGameBoard(String game,GameSelectorWindow gameSelectorWindow) {

        //cellPanel = new JPanel();
        JPanel gamePanel = new JPanel();
        if(game.toLowerCase().equals("tic tac toe")) {
            gamePanel = createGridPanel(TICTACTOE_SIZE, TICTACTOE_SIZE);
        }
        if(game.toLowerCase().equals("reversi")) {
            gamePanel = createGridPanel(REVERSI_SIZE, REVERSI_SIZE);
        }

        cellPanel.removeAll();
        cellPanel.add(gamePanel);
        cellPanel.revalidate();
        cellPanel.repaint();
        //tttPanel.add(cellPanel);
        backToMainMenuButton.addActionListener((
                        ActionEvent e) -> {
            gameSelectorWindow.returnToMainMenu();
            System.out.println("gothere");
        });
    }
    //Set the IP, game name and name
    public void setIGN(String ip, String gameName, String name) {
        this.ip.setText(ip);
        this.gameName.setText(gameName);
        this.name.setText(name);
    }
    public JPanel getTTTPanel() {
        return tttPanel;
    }
    //Creates a grid of buttons and adds a global event bus event on click with the index of the button.
    private JPanel createGridPanel(int sizeX, int sizeY) {
        JPanel cellPanel = new JPanel(new GridLayout(sizeX,sizeY));
        cells = new JButton[sizeX*sizeY];
        for(int i =0; i < sizeX*sizeY; i++){
            cells[i] = new JButton(" ");
            cells[i].setPreferredSize(new Dimension(1000/sizeX,1000/sizeY));
            cells[i].setFont(new Font("Arial", Font.BOLD, 480/sizeX));
            cells[i].setFocusPainted(false);
            cellPanel.add(cells[i]);
            final int index = i;
            cells[i].addActionListener((ActionEvent e) -> {
                setCell(index,"X");//■                                      todo get current player
                GlobalEventBus.post(new Events.ServerEvents.CellClicked(index));
                logger.info("Grid button {} was clicked.", index);
            });
        }
        return cellPanel;
    }
    public void setCell(int cell, String newValue){
        cells[cell].setText(newValue);
    }
}
