package research;

public class AIData {
    public String AI;
    public long gamesPlayed;
    public double winrate;
    public double averageIterations;
    public double averageIterations10;
    public double averageIterations20;
    public double averageIterations30;

    public AIData(String AI, long gamesPlayed, double winrate, double averageIterations, double averageIterations10, double averageIterations20, double averageIterations30) {
        this.AI = AI;
        this.gamesPlayed = gamesPlayed;
        this.winrate = winrate;
        this.averageIterations = averageIterations;
        this.averageIterations10 = averageIterations10;
        this.averageIterations20 = averageIterations20;
        this.averageIterations30 = averageIterations30;
    }

    public String getAI() {
        return AI;
    }

    public void setAI(String AI) {
        this.AI = AI;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public double getWinrate() {
        return winrate;
    }

    public void setWinrate(double winrate) {
        this.winrate = winrate;
    }

    public double getAverageIterations() {
        return averageIterations;
    }

    public void setAverageIterations(double averageIterations) {
        this.averageIterations = averageIterations;
    }

    public double getAverageIterations10() {
        return averageIterations10;
    }

    public void setAverageIterations10(double averageIterations10) {
        this.averageIterations10 = averageIterations10;
    }

    public double getAverageIterations20() {
        return averageIterations20;
    }

    public void setAverageIterations20(double averageIterations20) {
        this.averageIterations20 = averageIterations20;
    }

    public double getAverageIterations30() {
        return averageIterations30;
    }

    public void setAverageIterations30(double averageIterations30) {
        this.averageIterations30 = averageIterations30;
    }
}
