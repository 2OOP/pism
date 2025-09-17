package org.toop.UI;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class GameSelectorWindow extends JFrame {
    private JPanel mainMenu;
    private JTextField nameTextField;
    private JTextField name2TextField;
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
        connectButton.addActionListener((ActionEvent e) -> {
            if(     !nameTextField.getText().isEmpty() &&
                    !name2TextField.getText().isEmpty()   &&
                    !ipTextField.getText().isEmpty() &&
                    !portTextField.getText().isEmpty()) {

                CompletableFuture<String> serverIdFuture = new CompletableFuture<>();
                GlobalEventBus.post(new Events.ServerEvents.StartServerRequest(
                        portTextField.getText(),
                        Objects.requireNonNull(gameSelectorBox.getSelectedItem()).toString().toLowerCase().replace(" ", ""),
                        serverIdFuture
                ));
                String serverId;
                try {
                    serverId = serverIdFuture.get();
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                } // TODO: Better error handling to not crash the system.

                CompletableFuture<String> connectionIdFuture = new CompletableFuture<>();
                GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest(
                        ipTextField.getText(),
                        portTextField.getText(),
                        connectionIdFuture
                ));
                String connectionId;
                try {
                    connectionId = connectionIdFuture.get();
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                } // TODO: Better error handling to not crash the system.

                CompletableFuture<String> ticTacToeGame = new CompletableFuture<>();
                GlobalEventBus.post(new Events.ServerEvents.CreateTicTacToeGameRequest( // TODO: Make this happen through commands send through the connection, instead of an event.
                        serverId,
                        nameTextField.getText(),
                        name2TextField.getText(),
                        ticTacToeGame
                ));
                String ticTacToeGameId;
                try {
                    ticTacToeGameId = ticTacToeGame.get();
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                } // TODO: Better error handling to not crash the system.


                GlobalEventBus.post(new Events.ServerEvents.RunTicTacToeGame(serverId, ticTacToeGameId)); // TODO: attempt connecting to the server with given ip, port and name.

                frame.remove(mainMenu);
                UIGameBoard ttt = new UIGameBoard(gameSelectorBox.getSelectedItem().toString(),this);
                frame.add(ttt.getTTTPanel());
                frame.revalidate();
                frame.repaint();
            } else {
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
