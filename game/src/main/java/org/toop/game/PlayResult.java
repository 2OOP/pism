package org.toop.game;

import org.toop.game.enumerators.GameState;

public record PlayResult(GameState state, int winner) {
}
