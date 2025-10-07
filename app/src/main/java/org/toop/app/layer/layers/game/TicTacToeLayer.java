package org.toop.app.layer.layers.game;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.app.layer.layers.MainLayer;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.Game;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.game.tictactoe.TicTacToeAI;
import org.toop.local.AppContext;

public final class TicTacToeLayer extends Layer {
    private TicTacToeCanvas canvas;

    private AtomicReference<TicTacToe> ticTacToe;
    private TicTacToeAI ticTacToeAI;

    private GameInformation information;

    private final Text currentPlayerNameText;
    private final Text currentPlayerMoveText;

    private final BlockingQueue<Game.Move> playerMoveQueue = new LinkedBlockingQueue<>();

    // Todo: set these from the server
    private char currentPlayerMove = Game.EMPTY;
    private String player2Name = "";

    final AtomicBoolean firstPlayerIsMe = new AtomicBoolean(true);

    public TicTacToeLayer(GameInformation information) {
        super("bg-primary");

        canvas =
                new TicTacToeCanvas(
                        Color.LIME,
                        (App.getHeight() / 100) * 75,
                        (App.getHeight() / 100) * 75,
                        (cell) -> {
                            try {
                                if (information.isConnectionLocal()) {
                                    if (ticTacToe.get().getCurrentTurn() == 0) {
                                        playerMoveQueue.put(new Game.Move(cell, 'X'));
                                    } else {
                                        playerMoveQueue.put(new Game.Move(cell, 'O'));
                                    }
                                } else {
                                    if (information.isPlayerHuman()[0]
                                            && currentPlayerMove != Game.EMPTY) {
                                        playerMoveQueue.put(
                                                new Game.Move(
                                                        cell, firstPlayerIsMe.get() ? 'X' : 'O'));
                                    }
                                }
                            } catch (InterruptedException _) {
                            }
                        });

        ticTacToe = new AtomicReference<>(new TicTacToe());
        ticTacToeAI = new TicTacToeAI();

        this.information = information;

        if (information.isConnectionLocal()) {
            new Thread(this::localGameThread).start();
        }

        currentPlayerNameText = NodeBuilder.header("");
        currentPlayerMoveText = NodeBuilder.header("");

        reload();
    }

    public TicTacToeLayer(GameInformation information, long clientID) {
        this(information);

        Thread a = new Thread(this::serverGameThread);
        a.setDaemon(false);
        a.start();

        reload();
    }

    @Override
    public void reload() {
        popAll();

        canvas.resize((App.getHeight() / 100) * 75, (App.getHeight() / 100) * 75);

        for (int i = 0; i < ticTacToe.get().board.length; i++) {
            final char value = ticTacToe.get().board[i];

            if (value == 'X') {
                canvas.drawX(Color.RED, i);
            } else if (value == 'O') {
                canvas.drawO(Color.BLUE, i);
            }
        }

        final var backButton =
                NodeBuilder.button(
                        AppContext.getString("back"),
                        () -> {
                            App.activate(new MainLayer());
                        });

        final Container controlContainer = new VerticalContainer(5);
        controlContainer.addNodes(backButton);

        final Container informationContainer = new HorizontalContainer(15);
        informationContainer.addNodes(currentPlayerNameText, currentPlayerMoveText);

        addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
        addContainer(informationContainer, Pos.TOP_LEFT, 2, 2, 0, 0);
        addGameCanvas(canvas, Pos.CENTER, 0, 0);
    }

    private int compurterDifficultyToDepth(int maxDifficulty, int difficulty) {
        return (int) (((float) maxDifficulty / difficulty) * 9);
    }

    private void localGameThread() {
        boolean running = true;

        while (running) {
            final int currentPlayer = ticTacToe.get().getCurrentTurn();

            currentPlayerNameText.setText(information.playerName()[currentPlayer]);
            currentPlayerMoveText.setText(ticTacToe.get().getCurrentTurn() == 0 ? "X" : "O");

            Game.Move move = null;

            if (information.isPlayerHuman()[currentPlayer]) {
                try {
                    final Game.Move wants = playerMoveQueue.take();
                    final Game.Move[] legalMoves = ticTacToe.get().getLegalMoves();

                    for (final Game.Move legalMove : legalMoves) {
                        if (legalMove.position() == wants.position()
                                && legalMove.value() == wants.value()) {
                            move = wants;
                        }
                    }
                } catch (InterruptedException _) {
                }
            } else {
                final long start = System.currentTimeMillis();

                move =
                        ticTacToeAI.findBestMove(
                                ticTacToe.get(),
                                compurterDifficultyToDepth(
                                        10, information.computerDifficulty()[currentPlayer]));

                if (information.computerThinkTime()[currentPlayer] > 0) {
                    final long elapsedTime = System.currentTimeMillis() - start;
                    final long sleepTime =
                            information.computerThinkTime()[currentPlayer] * 1000L - elapsedTime;

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException _) {
                    }
                }
            }

            if (move == null) {
                continue;
            }

            final Game.State state = ticTacToe.get().play(move);

            if (move.value() == 'X') {
                canvas.drawX(Color.RED, move.position());
            } else if (move.value() == 'O') {
                canvas.drawO(Color.BLUE, move.position());
            }

            if (state != Game.State.NORMAL) {
                if (state == Game.State.WIN) {
                    App.push(
                            new GameFinishedPopup(
                                    false,
                                    information.playerName()[ticTacToe.get().getCurrentTurn()]));
                } else if (state == Game.State.DRAW) {
                    App.push(new GameFinishedPopup(true, ""));
                }

                running = false;
            }
        }
    }

    private void serverGameThread() {
        new EventFlow()
                .listen(this::handleServerGameStart) // <-----------
                .listen(this::yourTurnResponse)
                .listen(this::onMoveResponse)
                .listen(this::handleReceivedMessage);
    }

    private void handleServerGameStart(NetworkEvents.GameMatchResponse resp) {
        // Meneer Bas de Jong. Dit functie wordt niet aangeroepen als je de challenger bent.
        // Ik heb veel dingen geprobeert. FUCKING veel dingen. Hij doet het niet.
        // Ik heb zelfs in jou code gekeken en unsubscribeAfterSuccess op false gezet. (zie
        // ConnectedLayer).
        // Alle andere functies worden wel gecalt. Behalve dit.

        // Ben jij gehandicapt of ik? Want het moet 1 van de 2 zijn. Ik ben dit al 2 uur aan het
        // debuggen.
        // Ik ga nu slapen (04:46).

        //                                ⠀⠀⠀⠀⠀⠀⣀⣀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⢀⣴⣿⣿⠿⣟⢷⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⢸⣏⡏⠀⠀⠀⢣⢻⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⢸⣟⠧⠤⠤⠔⠋⠀⢿⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⠀⣿⡆⠀⠀⠀⠀⠀⠸⣷⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⠀⠘⣿⡀⢀⣶⠤⠒⠀⢻⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⠀⠀⢹⣧⠀⠀⠀⠀⠀⠈⢿⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⠀⠀⠀⣿⡆⠀⠀⠀⠀⠀⠈⢿⣆⣠⣤⣤⣤⣤⣴⣦⣄⡀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⠀⢀⣾⢿⢿⠀⠀⠀⢀⣀⣀⠘⣿⠋⠁⠀⠙⢇⠀⠀⠙⢿⣦⡀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⢀⣾⢇⡞⠘⣧⠀⢖⡭⠞⢛⡄⠘⣆⠀⠀⠀⠈⢧⠀⠀⠀⠙⢿⣄⠀⠀⠀⠀
        //                                ⠀⠀⣠⣿⣛⣥⠤⠤⢿⡄⠀⠀⠈⠉⠀⠀⠹⡄⠀⠀⠀⠈⢧⠀⠀⠀⠈⠻⣦⠀⠀⠀
        //                                ⠀⣼⡟⡱⠛⠙⠀⠀⠘⢷⡀⠀⠀⠀⠀⠀⠀⠹⡀⠀⠀⠀⠈⣧⠀⠀⠀⠀⠹⣧⡀⠀
        //                                ⢸⡏⢠⠃⠀⠀⠀⠀⠀⠀⢳⡀⠀⠀⠀⠀⠀⠀⢳⡀⠀⠀⠀⠘⣧⠀⠀⠀⠀⠸⣷⡀
        //                                ⠸⣧⠘⡇⠀⠀⠀⠀⠀⠀⠀⢳⡀⠀⠀⠀⠀⠀⠀⢣⠀⠀⠀⠀⢹⡇⠀⠀⠀⠀⣿⠇
        //                                ⠀⣿⡄⢳⠀⠀⠀⠀⠀⠀⠀⠈⣷⠀⠀⠀⠀⠀⠀⠈⠆⠀⠀⠀⠀⠀⠀⠀⠀⣼⡟⠀
        //                                ⠀⢹⡇⠘⣇⠀⠀⠀⠀⠀⠀⠰⣿⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡄⠀⣼⡟⠀⠀
        //                                ⠀⢸⡇⠀⢹⡆⠀⠀⠀⠀⠀⠀⠙⠁⠀⠀⠀⠀⠀⠀⠀⠀⡀⠀⠀⠀⢳⣼⠟⠀⠀⠀
        //                                ⠀⠸⣧⣀⠀⢳⡀⠀⠀⠀⠀⠀⠀⠀⡄⠀⠀⠀⠀⠀⠀⠀⢃⠀⢀⣴⡿⠁⠀⠀⠀⠀
        //                                ⠀⠀⠈⠙⢷⣄⢳⡀⠀⠀⠀⠀⠀⠀⢳⡀⠀⠀⠀⠀⠀⣠⡿⠟⠛⠉⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⠀⠈⠻⢿⣷⣦⣄⣀⣀⣠⣤⠾⠷⣦⣤⣤⡶⠟⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        //                                ⠀⠀⠀⠀⠀⠀⠀⠈⠉⠛⠛⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀

        player2Name = resp.opponent();
        System.out.println(player2Name);

        currentPlayerMoveText.setText("X");

        if (!resp.playerToMove().equalsIgnoreCase(resp.opponent())) {
            currentPlayerNameText.setText(information.playerName()[0]);
            firstPlayerIsMe.set(true);

            System.out.printf("I am starting: My client id is %d\n", resp.clientId());
        } else {
            currentPlayerNameText.setText(player2Name);
            firstPlayerIsMe.set(false);

            System.out.printf("I am NOT starting: My client id is %d\n", resp.clientId());
        }
    }

    private void onMoveResponse(NetworkEvents.GameMoveResponse resp) {
        char playerChar;

        if (!resp.player().equalsIgnoreCase(player2Name)) {
            playerChar = firstPlayerIsMe.get() ? 'X' : 'O';
        } else {
            playerChar = firstPlayerIsMe.get() ? 'O' : 'X';
        }

        final Game.Move move = new Game.Move(Integer.parseInt(resp.move()), playerChar);
        final Game.State state = ticTacToe.get().play(move);

        if (state
                != Game.State.NORMAL) { // todo differentiate between future draw guaranteed and is
            // currently a draw
            if (state == Game.State.WIN) {
                App.push(
                        new GameFinishedPopup(
                                false, information.playerName()[ticTacToe.get().getCurrentTurn()]));
            } else if (state == Game.State.DRAW) {
                App.push(new GameFinishedPopup(true, ""));
            }
        }

        if (move.value() == 'X') {
            canvas.drawX(Color.RED, move.position());
        } else if (move.value() == 'O') {
            canvas.drawO(Color.BLUE, move.position());
        }

        currentPlayerNameText.setText(
                ticTacToe.get().getCurrentTurn() == (firstPlayerIsMe.get() ? 0 : 1)
                        ? information.playerName()[0]
                        : player2Name);
        currentPlayerMoveText.setText(ticTacToe.get().getCurrentTurn() == 0 ? "X" : "O");
    }

    private void yourTurnResponse(NetworkEvents.YourTurnResponse response) {
        int position = -1;

        if (information.isPlayerHuman()[0]) {
            try {
                position = playerMoveQueue.take().position();
            } catch (InterruptedException _) {
            }
        } else {
            final Game.Move move =
                    ticTacToeAI.findBestMove(
                            ticTacToe.get(),
                            compurterDifficultyToDepth(10, information.computerDifficulty()[0]));

            position = move.position();
        }

        new EventFlow()
                .addPostEvent(new NetworkEvents.SendMove(response.clientId(), (short) position))
                .postEvent();
    }

    private void handleReceivedMessage(NetworkEvents.ReceivedMessage msg) {
        System.out.println("Received Message: " + msg.message()); // todo add chat window
    }
}
