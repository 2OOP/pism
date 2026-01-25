package research;

import org.toop.framework.game.players.ArtificialPlayer;

import java.util.ArrayList;
import java.util.List;

public class Matchup {
    public ArtificialPlayer player1;
    public ArtificialPlayer player2;

    public Matchup(ArtificialPlayer player1, ArtificialPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Matchup() {}

    public String toString() {
        return player1.toString() + " VS " + player2.toString();
    }

    public ArtificialPlayer getPlayer1() {
        return player1;
    }

    public ArtificialPlayer getPlayer2() {
        return player2;
    }
}
