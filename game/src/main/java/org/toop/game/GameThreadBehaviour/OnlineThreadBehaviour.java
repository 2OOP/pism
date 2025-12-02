package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.GUIEvents;
import org.toop.framework.gameFramework.abstractClasses.GameR;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.gameFramework.abstractClasses.TurnBasedGameR;
import org.toop.framework.gameFramework.interfaces.SupportsOnlinePlay;
import org.toop.game.players.AbstractPlayer;
import org.toop.game.players.OnlinePlayer;

/**
 * Handles online multiplayer game logic.
 * <p>
 * Reacts to server events, sending moves and updating the game state
 * for the local player while receiving moves from other players.
 */
public class OnlineThreadBehaviour extends ThreadBehaviourBase implements SupportsOnlinePlay {

    /** The local player controlled by this client. */
    private AbstractPlayer mainPlayer;

    /**
     * Creates behaviour and sets the first local player
     * (non-online player) from the given array.
     */
    public OnlineThreadBehaviour(TurnBasedGameR game, AbstractPlayer[] players) {
        super(game, players);
        this.mainPlayer = getFirstNotOnlinePlayer(players);
    }

    /** Finds the first non-online player in the array. */
    private AbstractPlayer getFirstNotOnlinePlayer(AbstractPlayer[] players) {
        for (AbstractPlayer player : players) {
            if (!(player instanceof OnlinePlayer)) {
                return player;
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
    public void yourTurn(NetworkEvents.YourTurnResponse event) {
        if (!isRunning.get()) return;
        int move = mainPlayer.getMove(game.clone());
        new EventFlow().addPostEvent(NetworkEvents.SendMove.class, event.clientId(), (short) move).postEvent();
    }

    /**
     * Handles a move received from the server for any player.
     * Updates the game state and triggers a UI refresh.
     */
    @Override
    public void moveReceived(NetworkEvents.GameMoveResponse event) {
        if (!isRunning.get()) return;
        game.play(Integer.parseInt(event.move()));
        new EventFlow().addPostEvent(GUIEvents.RefreshGameCanvas.class).postEvent();
    }

    /**
     * Handles the end of the game as notified by the server.
     * Updates the UI to show a win or draw result for the local player.
     */
    @Override
    public void gameFinished(NetworkEvents.GameResultResponse event) {
        switch(event.condition().toUpperCase()){
            case "WIN" -> new EventFlow().addPostEvent(GUIEvents.GameEnded.class, true, mainPlayer.getPlayerIndex()).postEvent();
            case "DRAW" -> new EventFlow().addPostEvent(GUIEvents.GameEnded.class, false, TurnBasedGameR.EMPTY).postEvent();
            case "LOSS" -> new EventFlow().addPostEvent(GUIEvents.GameEnded.class, true, (mainPlayer.getPlayerIndex() + 1)%2).postEvent();
            default -> {
                logger.error("Invalid condition");
                throw new RuntimeException("Unknown condition");
            }
        }
    }
}
