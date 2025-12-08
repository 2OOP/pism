package org.toop.framework.gameFramework.model.game.threadBehaviour;

import org.toop.framework.gameFramework.LongPairConsumer;
import org.toop.framework.gameFramework.controller.GameController;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

import java.util.function.Consumer;

/**
 * Strategy interface for controlling game thread behavior.
 * <p>
 * Defines how a game's execution is started, stopped, and which player is active.
 */
public interface ThreadBehaviour extends Controllable {
    void setOnUpdateUI(Runnable onUpdateUI);
    void setOnSendMove(LongPairConsumer onSendMove);
}

