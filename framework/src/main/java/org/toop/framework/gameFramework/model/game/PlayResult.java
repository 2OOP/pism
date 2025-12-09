package org.toop.framework.gameFramework.model.game;

import org.toop.framework.gameFramework.GameState;

/**
 * Represents the result of a move in a turn-based game.
 *
 * @param state  the resulting {@link GameState} after the move
 * @param player the index of the player associated with the result (winner or relevant player)
 */
public record PlayResult(GameState state, int player) {
}
