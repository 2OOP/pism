package org.toop.game.interfaces;

import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;

public interface IPlayableR {
    Integer[] getLegalMoves();
    GameState play(int move);
}
