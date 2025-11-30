package org.toop.framework.gameFramework.interfaces;

import org.toop.framework.networking.events.NetworkEvents;

public interface SupportsOnlinePlay {
    void yourTurn(NetworkEvents.YourTurnResponse event) ;
    void moveReceived(NetworkEvents.GameMoveResponse event);
    void gameFinished(NetworkEvents.GameResultResponse event);
}
