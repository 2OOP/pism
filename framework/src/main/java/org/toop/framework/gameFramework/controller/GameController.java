package org.toop.framework.gameFramework.controller;

import org.toop.framework.networking.connection.events.NetworkEvents;

public interface GameController {
    /** Called when it is this player's turn to make a move. */
    void onYourTurn(NetworkEvents.YourTurnResponse event);

    /** Called when a move from another player is received. */
    void onMoveReceived(NetworkEvents.GameMoveResponse event);

    /** Called when the game has finished, with the final result. */
    void gameFinished(NetworkEvents.GameResultResponse event);

    void start();

    void stop();

    /** Called to refresh or update the game UI. */
    void updateUI();

    void sendMove(long clientId, long move);
}
