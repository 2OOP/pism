package org.toop.framework.networking.server.tournaments.matchmakers;

import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.TournamentMatch;
import org.toop.framework.networking.server.tournaments.shufflers.Shuffler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class RoundRobinMatchMaker implements MatchMaker {

    private final List<NettyClient> players = new ArrayList<>();

    public RoundRobinMatchMaker() {} // TODO let user decide store type

    @Override
    public void addPlayer(NettyClient player) {
        players.addLast(player);
    }

    @Override
    public void shuffle(Shuffler shuffler) {
        if (players.size() < 2) return;
        shuffler.shuffle(players);
    }

    @Override
    public List<NettyClient> getPlayers() {
        return players;
    }

    @Override
    public Iterator<TournamentMatch> iterator() {
        return new Iterator<>() {

            private int i = 0;
            private int j = 1;
            private boolean reverse = false;

            @Override
            public boolean hasNext() {
                return players.size() > 1
                        && i < players.size() - 1
                        && j < players.size();
            }

            @Override
            public TournamentMatch next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                NettyClient home = players.get(i);
                NettyClient away = players.get(j);

                TournamentMatch match = reverse ? new TournamentMatch(away, home) : new TournamentMatch(home, away);

                advance();
                return match;
            }

            private void advance() {
                j++;
                if (j >= players.size()) {
                    i++;
                    j = i + 1;

                    if (i >= players.size() - 1) {
                        if (!reverse) {
                            reverse = true;
                            i = 0;
                            j = 1;
                        }
                    }
                }
            }
        };
    }
}