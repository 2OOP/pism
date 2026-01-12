package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.tournaments.scoresystems.ScoreSystem;

import java.util.List;

@FunctionalInterface
public interface ResultBroadcaster<T extends ScoreSystem<?, ?, ?>> {
    void broadcast(List<T> scoreSystem);
}
