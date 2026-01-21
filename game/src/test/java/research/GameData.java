package research;

public record GameData(
        String AI1,
        String AI2,
        String winner,
        int turns,

        int AI1totalIterations,
        double AI1averageIterations,
        double AI1averageIterations10,
        double AI1averageIterations20,
        double AI1averageIterations30,

        int AI2totalIterations,
        double AI2averageIterations,
        double AI2averageIterations10,
        double AI2averageIterations20,
        double AI2averageIterations30,

        long millisecondsAI1,
        long millisecondsAI2,

        String time
) {}

