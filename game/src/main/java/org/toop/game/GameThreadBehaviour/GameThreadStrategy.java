package org.toop.game.GameThreadBehaviour;

import org.toop.game.players.AbstractPlayer;

public interface GameThreadStrategy {
    void start();
    void stop();
    AbstractPlayer getCurrentPlayer();
}
