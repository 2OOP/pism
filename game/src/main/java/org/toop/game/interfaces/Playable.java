package org.toop.game.interfaces;

import org.toop.game.Game;
import org.toop.game.enumerators.GameState;

public interface Playable {
    Game.Move[] getLegalMoves();
    GameState play(Game.Move move);
}
