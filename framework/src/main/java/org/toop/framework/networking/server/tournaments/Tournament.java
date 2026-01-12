package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.MatchExecutor;
import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.IntegerScoreSystem;
import org.toop.framework.networking.server.tournaments.shufflers.Shuffler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Tournament {

    private final MatchExecutor matchExecutor;
    private final List<IntegerScoreSystem> scoreSystems;
    private final TournamentRunner tournamentRunner;
    private final MatchMaker matchMaker;
    private final ResultBroadcaster<IntegerScoreSystem> broadcaster;
    private final NettyClient[] players;
    private final Duration turnTime;
    private final Shuffler shuffler;

    private Tournament(Tournament.Builder builder) {
        matchExecutor = builder.matchExecutor;
        scoreSystems = builder.scoreSystems;
        tournamentRunner = builder.tournamentRunner;
        matchMaker = builder.matchMaker;
        broadcaster = builder.broadcaster;
        players = builder.players;
        turnTime = builder.turnTime;
        shuffler = builder.shuffler;
    }

    public void run(String gameType) {

        Arrays.stream(players).forEach(e -> {
            matchMaker.addPlayer(e);
            scoreSystems.forEach(k -> k.addPlayer(e));
        });

        if (shuffler != null) matchMaker.shuffle(shuffler);

        tournamentRunner.run(matchExecutor, matchMaker, scoreSystems, broadcaster, turnTime, gameType);

    }

    public static class Builder {
        private MatchExecutor matchExecutor;
        private List<IntegerScoreSystem> scoreSystems = new ArrayList<>();
        private TournamentRunner tournamentRunner;
        private MatchMaker matchMaker;
        private ResultBroadcaster<IntegerScoreSystem> broadcaster;
        private NettyClient[] players;
        private NettyClient[] observors;
        private NettyClient[] admins;
        private Duration turnTime = Duration.ofSeconds(10);
        private Shuffler shuffler;

        public Builder matchExecutor(MatchExecutor matchExecutor) {
            this.matchExecutor = matchExecutor;
            return this;
        }

        public Builder addScoreSystem(IntegerScoreSystem scoreSystem) {
            this.scoreSystems.addLast(scoreSystem);
            return this;
        }

        public Builder tournamentRunner(TournamentRunner tournamentRunner) {
            this.tournamentRunner = tournamentRunner;
            return this;
        }

        public Builder matchMaker(MatchMaker matchMaker) {
            this.matchMaker = matchMaker;
            return this;
        }

        public Builder resultBroadcaster(ResultBroadcaster<IntegerScoreSystem> broadcaster) {
            this.broadcaster = broadcaster;
            return this;
        }

        public Builder addPlayers(NettyClient[] players) {
            this.players = players;
            return this;
        }

        public Builder addObservers(NettyClient[] observors) { // TODO
            this.observors = observors;
            return this;
        }

        public Builder addAdmins(NettyClient[] admins) { // TODO
            this.admins = admins;
            return this;
        }

        public Builder turnTimeout(Duration turnTime) {
            this.turnTime = turnTime;
            return this;
        }

        public Builder addMatchShuffler(Shuffler shuffler) {
            this.shuffler = shuffler;
            return this;
        }

        public Tournament build() {
            Objects.requireNonNull(matchExecutor, "matchExecutor");
            Objects.requireNonNull(tournamentRunner, "tournamentRunner");
            Objects.requireNonNull(matchMaker, "matchMaker");
            Objects.requireNonNull(broadcaster, "resultBroadcaster"); // TODO is not always necessary and needs to be more generic, not just at the end
            Objects.requireNonNull(players, "players");
            return new Tournament(this);
        }
    }
}
