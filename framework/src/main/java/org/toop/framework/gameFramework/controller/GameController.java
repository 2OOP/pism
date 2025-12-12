package org.toop.framework.gameFramework.controller;

import org.toop.framework.gameFramework.model.game.threadBehaviour.Controllable;
import org.toop.framework.networking.connection.events.NetworkEvents;

public interface GameController extends Controllable, UpdatesGameUI {
    /** Called when it is this player's turn to make a move. */
    void onYourTurn(NetworkEvents.YourTurnResponse event);

    /** Called when a move from another player is received. */
    void onMoveReceived(NetworkEvents.GameMoveResponse event);

    /** Called when the game has finished, with the final result. */
    void gameFinished(NetworkEvents.GameResultResponse event);

    void sendMove(long clientId, long move);
}
