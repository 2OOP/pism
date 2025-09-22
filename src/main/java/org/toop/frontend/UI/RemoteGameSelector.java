package org.toop.frontend.UI;

import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.eventbus.events.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.eventbus.events.NetworkEvents;
import org.toop.frontend.games.LocalTicTacToe;
import org.toop.frontend.networking.NetworkingGameClientHandler;

public class RemoteGameSelector {
    private static final Logger logger = LogManager.getLogger(RemoteGameSelector.class);

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

    private LocalTicTacToe localTicTacToe;

    public RemoteGameSelector() {
        gameSelectorBox.addItem("Tic Tac Toe");
        gameSelectorBox.addItem("Reversi");
        // todo get supported games from server and add to gameSelectorBox
        frame = new JFrame("Game Selector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setResizable(true);

        init();
        frame.add(mainMenu);
        frame.setVisible(true);
        // GlobalEventBus.subscribeAndRegister() Todo add game panel to frame when connection
        // succeeds

    }

    private void init() {
        connectButton.addActionListener(
                (ActionEvent e) -> {
                    if (!nameTextField.getText().isEmpty()
                            && !name2TextField.getText().isEmpty()
                            && !ipTextField.getText().isEmpty()
                            && !portTextField.getText().isEmpty()) {

                        CompletableFuture<String> serverIdFuture = new CompletableFuture<>();
                        GlobalEventBus.post(
                                new Events.ServerEvents.StartServerRequest(
                                        Integer.parseInt(portTextField.getText()), // TODO: Unsafe parse
                                        Objects.requireNonNull(gameSelectorBox.getSelectedItem())
                                                .toString()
                                                .toLowerCase()
                                                .replace(" ", ""),
                                        serverIdFuture));
                        String serverId;
                        try {
                            serverId = serverIdFuture.get();
                        } catch (InterruptedException | ExecutionException ex) {
                            throw new RuntimeException(ex);
                        } // TODO: Better error handling to not crash the system.

                        CompletableFuture<String> connectionIdFuture = new CompletableFuture<>();
                        GlobalEventBus.post(
                                new NetworkEvents.StartClientRequest(
                                        NetworkingGameClientHandler::new,
                                        ipTextField.getText(),
                                        Integer.parseInt(portTextField.getText()), // TODO: Not safe parsing
                                        connectionIdFuture));
                        String connectionId;
                        try {
                            connectionId = connectionIdFuture.get();
                        } catch (InterruptedException | ExecutionException ex) {
                            throw new RuntimeException(ex);
                        } // TODO: Better error handling to not crash the system.

                        GlobalEventBus.subscribeAndRegister(
                                NetworkEvents.ReceivedMessage.class,
                                event -> {
                                    if (event.message().equalsIgnoreCase("ok")) {
                                        logger.info("received ok from server.");
                                    } else if (event.message().toLowerCase().startsWith("gameid")) {
                                        String gameId =
                                                event.message()
                                                        .toLowerCase()
                                                        .replace("gameid ", "");
                                        GlobalEventBus.post(
                                                new NetworkEvents.SendCommand(
                                                        "start_game " + gameId));
                                    } else {
                                        logger.info("{}", event.message());
                                    }
                                });

                        GlobalEventBus.post(
                                new NetworkEvents.SendCommand(
                                        connectionId,
                                        "create_game",
                                        nameTextField.getText(),
                                        name2TextField.getText()));

                        //                CompletableFuture<String> ticTacToeGame = new
                        // CompletableFuture<>();
                        //                GlobalEventBus.post(new
                        // Events.ServerEvents.CreateTicTacToeGameRequest( // TODO: Make this happen
                        // through commands send through the connection, instead of an event.
                        //                        serverId,
                        //                        nameTextField.getText(),
                        //                        name2TextField.getText(),
                        //                        ticTacToeGame
                        //                ));
                        //                String ticTacToeGameId;
                        //                try {
                        //                    ticTacToeGameId = ticTacToeGame.get();
                        //                } catch (InterruptedException | ExecutionException ex) {
                        //                    throw new RuntimeException(ex);
                        //                } // TODO: Better error handling to not crash the system.

                        frame.remove(mainMenu);
                        localTicTacToe =
                                LocalTicTacToe.createRemote(
                                        ipTextField.getText(), Integer.parseInt(portTextField.getText())); // TODO: Unsafe parse
                        UIGameBoard ttt = new UIGameBoard(localTicTacToe, this); // TODO: Fix later
                        frame.add(ttt.getTTTPanel()); // TODO: Fix later
                        frame.revalidate();
                        frame.repaint();
                    } else {
                        fillAllFields.setVisible(true);
                    }
                });
    }

    public void showMainMenu() {
        frame.removeAll();
        frame.add(mainMenu);
        frame.revalidate();
        frame.repaint();
    }
}
