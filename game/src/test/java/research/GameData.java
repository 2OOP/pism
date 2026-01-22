package research;

public record GameData(
        String AI1,
        String AI2,
        String winner,
        int turns,

        long AI1totalIterations,
        long AI1totalIterations10,
        long AI1totalIterations20,
        long AI1totalIterations30,
        double AI1averageIterations,
        double AI1averageIterations10,
        double AI1averageIterations20,
        double AI1averageIterations30,

        long AI2totalIterations,
        long AI2totalIterations10,
        long AI2totalIterations20,
        long AI2totalIterations30,
        double AI2averageIterations,
        double AI2averageIterations10,
        double AI2averageIterations20,
        double AI2averageIterations30,

        long millisecondsAI1,
        long millisecondsAI2,

        String time
) {}

