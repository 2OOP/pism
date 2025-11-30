package org.toop.game.GameThreadBehaviour;

import org.toop.framework.gameFramework.TurnBasedGameR;
import org.toop.game.players.AbstractPlayer;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ThreadBehaviourBase implements GameThreadStrategy{
    protected final AtomicBoolean isRunning = new AtomicBoolean();
    protected final TurnBasedGameR game;

    public ThreadBehaviourBase(TurnBasedGameR game) {
        this.game = game;
    }
}
