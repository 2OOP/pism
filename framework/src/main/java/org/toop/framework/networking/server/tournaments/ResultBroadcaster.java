package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.tournaments.scoresystems.ScoreSystem;

@FunctionalInterface
public interface ResultBroadcaster<T extends ScoreSystem<?, ?, ?>> {
    void broadcast(T scoreSystem);
}
