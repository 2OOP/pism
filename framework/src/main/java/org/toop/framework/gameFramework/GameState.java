package org.toop.framework.gameFramework;

/**
 * Represents the current state of a turn-based game.
 */
public enum GameState {
    /** OnlineTurnBasedGame is ongoing and no special condition applies. */
    NORMAL,

    /** OnlineTurnBasedGame ended in a draw. */
    DRAW,

    /** OnlineTurnBasedGame ended with a win for a player. */
    WIN,

    /** Next player's turn was skipped. */
    TURN_SKIPPED,
}
