package org.toop.game.interfaces;

import org.toop.game.Game;
import org.toop.game.records.Move;

public interface IAIMove <T extends Game>{
    Move findBestMove(T game, int depth);
}
