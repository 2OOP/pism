package org.toop.framework.networking.server.tournaments.scoresystems;

import java.util.Map;

public interface ScoreSystem<MATCHTYPE, SCORETYPE, USERTYPE> {
    String scoreName();
    void addPlayer(USERTYPE user);
    void result(MATCHTYPE match, SCORETYPE result);
    Map<USERTYPE, SCORETYPE> getScore();
}
