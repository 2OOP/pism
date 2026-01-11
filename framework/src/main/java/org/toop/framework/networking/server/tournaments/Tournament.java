package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.ScoreSystem;

import java.util.Objects;

public class Tournament {
    private final Server server;

    private final ScoreSystem scoreSystem;
    private final TournamentRunner tournamentRunner;
    private final MatchMaker matchMaker;

    private Tournament(Tournament.Builder builder) {
        server = builder.server;
        scoreSystem = builder.scoreSystem;
        tournamentRunner = builder.tournamentRunner;
        matchMaker = builder.matchMaker;
    }

    public void run(String gameType) throws IllegalArgumentException {
        if (server.gameTypes().stream().noneMatch(e -> e.equalsIgnoreCase(gameType)))
            throw new IllegalArgumentException("Invalid game type");

        tournamentRunner.run(server, matchMaker, scoreSystem, gameType);
    }

    public static class Builder {
        private Server server;
        private ScoreSystem scoreSystem;
        private TournamentRunner tournamentRunner;
        private MatchMaker matchMaker;

        public Builder server(Server server) {
            this.server = server;
            return this;
        }

        public Builder scoreSystem(ScoreSystem scoreSystem) {
            this.scoreSystem = scoreSystem;
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

        public Tournament build() {
            Objects.requireNonNull(server, "server");
            Objects.requireNonNull(scoreSystem, "scoreSystem");
            Objects.requireNonNull(tournamentRunner, "tournamentRunner");
            Objects.requireNonNull(matchMaker, "matchMaker");
            return new Tournament(this);
        }
    }
}
