package org.toop.framework.gameFramework;

import org.toop.framework.eventbus.events.EventsBase;
import org.toop.framework.eventbus.events.GenericEvent;

/**
 * Defines GUI-related events for the event bus.
 * <p>
 * These events notify the UI about updates such as game progress,
 * player actions, and game completion.
 */
public class GUIEvents extends EventsBase {

    /** Event to refresh or redraw the game canvas. */
    public record RefreshGameCanvas() implements GenericEvent {}

    /**
     * Event indicating the game has ended.
     *
     * @param winOrTie true if the game ended in a win, false for a draw
     * @param winner the index of the winning player, or -1 if no winner
     */
    public record GameEnded(boolean winOrTie, int winner) implements GenericEvent {}

    /** Event indicating a player has attempted a move. */
    public record PlayerAttemptedMove(int move) implements GenericEvent {}

    /** Event indicating a player is hovering over a move (for UI feedback). */
    public record PlayerMoveHovered(int move) implements GenericEvent {}
}
