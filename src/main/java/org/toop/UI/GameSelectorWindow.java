package org.toop.UI;
import org.toop.eventbus.GlobalEventBus;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;


public class GameSelectorWindow extends JFrame {
    private JPanel mainMenu;
    private JTextField nameTextField;
    private JTextField ipTextField;
    private JTextField portTextField;
    private JButton connectButton;
    private JComboBox gameSelectorBox;
    private JPanel cards;
    private JPanel gameSelector;
    private JFrame frame;
    private JLabel fillAllFields;

    public  GameSelectorWindow() {
        gameSelectorBox.addItem("Tic Tac Toe");
        gameSelectorBox.addItem("Reversi");
        //todo get supported games from server and add to gameSelectorBox
        frame = new JFrame("Game Selector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setResizable(true);

        init();
        frame.add(mainMenu);
        frame.setVisible(true);
        //GlobalEventBus.subscribeAndRegister() Todo add game panel to frame when connection succeeds

    }
    private void init() {
        connectButton.addActionListener((
                ActionEvent e) -> {
            if(!nameTextField.getText().equals("") && !ipTextField.getText().equals("") && !portTextField.getText().equals("")) {
                System.out.println(gameSelectorBox.getSelectedItem().toString()); //todo attempt connecting to the server with given ip, port and name.
                frame.remove(mainMenu);
                UIGameBoard ttt = new UIGameBoard(gameSelectorBox.getSelectedItem().toString(),this);
                frame.add(ttt.getTTTPanel());
                frame.revalidate();
                frame.repaint();
            }else{
                fillAllFields.setVisible(true);
            }
        });
    }
    public void returnToMainMenu() {
        frame.removeAll();
        frame.add(mainMenu);
        frame.revalidate();
        frame.repaint();
    }
}
