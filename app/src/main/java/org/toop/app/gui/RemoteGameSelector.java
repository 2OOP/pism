package org.toop.app.gui;

import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.tictactoe.LocalTicTacToe;
import org.toop.framework.networking.NetworkingGameClientHandler;
import org.toop.tictactoe.gui.UIGameBoard;

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

                        AtomicReference<String> clientId = new AtomicReference<>();
                        new EventFlow().addPostEvent(
                                NetworkEvents.StartClient.class,
                                (Supplier<NetworkingGameClientHandler>) NetworkingGameClientHandler::new,
                                "127.0.0.1",
                                5001
                            ).onResponse(
                                NetworkEvents.StartClientSuccess.class,
                                (response) -> {
                                    clientId.set(response.clientId());
                                }
                            ).asyncPostEvent();

//                        GlobalEventBus.subscribeAndRegister(
//                                NetworkEvents.ReceivedMessage.class,
//                                event -> {
//                                    if (event.message().equalsIgnoreCase("ok")) {
//                                        logger.info("received ok from server.");
//                                    } else if (event.message().toLowerCase().startsWith("gameid")) {
//                                        String gameId =
//                                                event.message()
//                                                        .toLowerCase()
//                                                        .replace("gameid ", "");
//                                        GlobalEventBus.post(
//                                                new NetworkEvents.SendCommand(
//                                                        "start_game " + gameId));
//                                    } else {
//                                        logger.info("{}", event.message());
//                                    }
//                                });
                        frame.remove(mainMenu);
                        UIGameBoard ttt = new UIGameBoard(localTicTacToe, this);
                        localTicTacToe.startThreads();
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
