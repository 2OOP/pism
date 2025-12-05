package org.toop.game.gameThreads;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.model.game.threadBehaviour.AbstractThreadBehaviour;
import org.toop.framework.gameFramework.view.GUIEvents;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.game.SupportsOnlinePlay;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.players.OnlinePlayer;

/**
 * Handles online multiplayer game logic.
 * <p>
 * Reacts to server events, sending moves and updating the game state
 * for the local player while receiving moves from other players.
 */
public class OnlineThreadBehaviour<T extends TurnBasedGame<T>> extends AbstractThreadBehaviour<T> implements SupportsOnlinePlay {
    /**
     * Creates behaviour and sets the first local player
     * (non-online player) from the given array.
     */
    public OnlineThreadBehaviour(T game) {
        super(game);
    }

    /** Finds the first non-online player in the array. */
    private int getFirstNotOnlinePlayer(Player<T>[] players) {
        for (int i = 0; i < players.length; i++) {
            if (!(players[i] instanceof OnlinePlayer)) {
                return i;
            }
        }
        throw new RuntimeException("All players are online players");
    }

    /** Starts processing network events for the local player. */
    @Override
    public void start() {
        isRunning.set(true);
    }

    /** Stops processing network events. */
    @Override
    public void stop() {
        isRunning.set(false);
    }

    /**
     * Called when the server notifies that it is the local player's turn.
     * Sends the generated move back to the server.
     */
    @Override
    public void onYourTurn(long clientId) {
        if (!isRunning.get()) return;
        long move = game.getPlayer(game.getCurrentTurn()).getMove(game.deepCopy());
        controller.sendMove(clientId, move);
    }

    /**
     * Handles a move received from the server for any player.
     * Updates the game state and triggers a UI refresh.
     */
    public void onMoveReceived(long move) {
        if (!isRunning.get()) return;
        game.play(move);
        new EventFlow().addPostEvent(GUIEvents.RefreshGameCanvas.class).postEvent();
    }

    /**
     * Handles the end of the game as notified by the server.
     * Updates the UI to show a win or draw result for the local player.
     */
    public void gameFinished(String condition) {
        switch(condition.toUpperCase()){
            case "WIN" -> new EventFlow().addPostEvent(GUIEvents.GameEnded.class, true, game.getCurrentTurn()).postEvent();
            case "DRAW" -> new EventFlow().addPostEvent(GUIEvents.GameEnded.class, false, -1).postEvent();
            case "LOSS" -> new EventFlow().addPostEvent(GUIEvents.GameEnded.class, true, (game.getCurrentTurn() + 1)%2).postEvent();
            default -> {
                logger.error("Invalid condition");
                throw new RuntimeException("Unknown condition");
            }
        }
    }
}
