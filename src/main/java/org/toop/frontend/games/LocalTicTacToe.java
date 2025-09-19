package org.toop.frontend.games;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

import java.util.concurrent.*;

/**
 * A representation of a local tic-tac-toe game.
 * Calls are made to a server for information about current game state.
 */
public class LocalTicTacToe { // TODO: Implement runnable
    private static final Logger logger = LogManager.getLogger(LocalTicTacToe.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<String> receivedQueue = new LinkedBlockingQueue<>();

    Object receivedMessageListener;

    volatile String gameId;
    volatile String connectionId;
    volatile String serverId;

    /**
     * Is either 1 or 2.
     */
    private int playersTurn = 1;

    /**
     * @return The current players turn.
     */
    public int getCurrentPlayersTurn() { return this.playersTurn; }


//    LocalTicTacToe(String gameId, String connectionId, String serverId) {
//        this.gameId = gameId;
//        this.connectionId = connectionId;
//        this.serverId = serverId;
//        this.receivedMessageListener = GlobalEventBus.subscribe(Events.ServerEvents.ReceivedMessage.class, this::receiveMessageAction);
//        GlobalEventBus.register(this.receivedMessageListener);
//
//
//        this.executor.submit(this::gameThread);
//    } TODO: If remote server

    /**
     *
     * @param isLocalServer If the server is hosted locally.
     * @param ip The IP of the server to connect to.
     * @param port The port of the server to connect to.
     */
    public LocalTicTacToe(boolean isLocalServer, String ip, String port) {
        this.receivedMessageListener = GlobalEventBus.subscribe(Events.ServerEvents.ReceivedMessage.class, this::receiveMessageAction);
        GlobalEventBus.register(this.receivedMessageListener);

        // TODO: Is blocking
        if  (isLocalServer) { this.serverId = this.createServer(port); }
        else                { this.serverId = null; } // TODO: What if null?
        this.connectionId = this.createConnection(ip, port);
        this.createGame(ip, port);

        this.executor.submit(this::gameThread);
    }

    private String createServer(String port) {
        CompletableFuture<String> serverIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartServerRequest(port, "tictactoe", serverIdFuture));
        try {
            return serverIdFuture.get();
        } catch (Exception e) {
            logger.error("Error getting server ID", e);
        }
        return null;
    }

    private String createConnection(String ip, String port) {
        CompletableFuture<String> connectionIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(new Events.ServerEvents.StartConnectionRequest(ip, port, connectionIdFuture)); // TODO: what if server couldn't be started with port.
        try {
            return connectionIdFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting connection ID", e);
        }
        return null;
    }

    private void createGame(String nameA, String nameB) {
        nameA = nameA.trim().replace(" ", "-");
        nameB = nameB.trim().replace(" ", "-");
        this.sendCommand("create_game", nameA, nameB);
    }

    private void startGame() {
        if (this.gameId == null) { return; }
        this.sendCommand("start_game", this.gameId);
    }

    /**
     * The game thread.
     */
    private void gameThread() {
        logger.info("Starting local game thread, connection: {}, server: {}", this.connectionId, this.serverId);

        CountDownLatch latch = new CountDownLatch(1); // TODO: This is bad, fix later

        new Thread(() -> {
            while(true) {
                String msg = this.receivedQueue.poll();
                if (msg == null) {continue;}
                if (msg.toLowerCase().startsWith("game created successfully")) {
                    String[] parts = msg.split("\\|");
                    String gameIdPart = parts[1];
                    this.gameId = gameIdPart.split(" ")[1];
                    latch.countDown();
                    break;
                }
            }
        }).start();

        try {
            latch.await(); // TODO: Bad, fix later
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        startGame(); // TODO: Actually need to wait, but is fine for now.
        boolean running = true;
        while (running) {
            try {
                String rec = this.receivedQueue.take();
                if (rec.equalsIgnoreCase("ok")) {continue;}
                else if (rec.equalsIgnoreCase("svr game yourturn")) {
                    if (this.playersTurn == 1) {
                        this.playersTurn += 1;
                    } else {
                        this.playersTurn -= 1;
                    }
                    logger.info("Player turn: {}", this.playersTurn);
                }
                else if (rec.equalsIgnoreCase("svr game win")) {
                    endListeners();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e); // TODO: Error handling
            }
        }

        this.endListeners();

    }

    /**
     * End the current game.
     */
    public void endGame() {
        sendCommand("gameid", "end_game"); // TODO: Command is a bit wrong.
    }

    /**
     * @param index The move to make.
     */
    public void move(int index) {
        sendCommand("gameid", this.gameId, "player", "test", "move", String.valueOf(index));
    }

    private void endTheGame() {
        this.sendCommand("end_game", this.gameId);
        this.endListeners();
    }

    private void receiveMessageAction(Events.ServerEvents.ReceivedMessage receivedMessage) {
        if (!receivedMessage.ConnectionUuid().equals(this.connectionId)) {
            return;
        }

        try {
            logger.info("Received message from " + this.connectionId + ": " + receivedMessage.message());
            this.receivedQueue.put(receivedMessage.message());
        } catch (InterruptedException e) {
            logger.error("Error waiting for received Message", e);
        }
    }

    private void sendCommand(String... args) {
        GlobalEventBus.post(new Events.ServerEvents.SendCommand(this.connectionId, args));
    }

    private void endListeners() {
        GlobalEventBus.unregister(this.receivedMessageListener);
    }

}
