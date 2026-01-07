package org.toop.framework.networking.server.stores;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.server.OnlineGame;

import java.util.Collection;
import java.util.List;

public class TurnBasedGameStore implements GameStore<OnlineGame<TurnBasedGame>, OnlineGame<TurnBasedGame>> {

    private List<OnlineGame<TurnBasedGame>> gameList;

    public TurnBasedGameStore(List<OnlineGame<TurnBasedGame>> initGameList) {
        this.gameList = initGameList;
    }

    @Override
    public void add(OnlineGame<TurnBasedGame> adding) {
        gameList.addLast(adding);
    }

    @Override
    public void remove(OnlineGame<TurnBasedGame> remover) {
        gameList.remove(remover);
    }

    @Override
    public OnlineGame<TurnBasedGame> get(OnlineGame<TurnBasedGame> getter) {
        return gameList.stream().filter(game->game.equals(getter)).findFirst().orElse(null);
    }

    @Override
    public Collection<OnlineGame<TurnBasedGame>> all() {
        return gameList;
    }
}
