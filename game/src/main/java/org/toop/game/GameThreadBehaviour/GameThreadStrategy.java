package org.toop.game.GameThreadBehaviour;

import org.toop.framework.networking.events.NetworkEvents;

public interface GameThreadStrategy {
    void start();
    void stop();
    void onYourTurn(NetworkEvents.YourTurnResponse response); // Assumes only one local player. This is based on given server documentation. Should allow for multiple local player inputs.
}
