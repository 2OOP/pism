package org.toop.game.GameThreadBehaviour;

import org.toop.game.players.Player;

public interface GameThreadStrategy {
    void start();
    void stop();
    Player getCurrentPlayer();
}
