package org.toop.framework.gameFramework.controller;

import org.toop.framework.gameFramework.model.game.SupportsOnlinePlay;
import org.toop.framework.gameFramework.model.game.threadBehaviour.Controllable;

public interface GameController extends Controllable, SupportsOnlinePlay, UpdatesGameUI {
}
