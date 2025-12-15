package org.toop.game.players.ai;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AI;
import org.toop.framework.gameFramework.model.player.AbstractAI;
import org.toop.game.games.reversi.BitboardReversi;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.random;

public class ReversiAIML<T extends TurnBasedGame<T>> extends AbstractAI<T> {

    MultiLayerNetwork model;

    public ReversiAIML() {
        InputStream is = getClass().getResourceAsStream("/reversi-model.zip");
        try {
            assert is != null;
            model = ModelSerializer.restoreMultiLayerNetwork(is);
        } catch (IOException e) {}
    }

    private int pickLegalMove(INDArray prediction, BitboardReversi reversi) {
        double[] logits = prediction.toDoubleVector();
        long legalMoves = reversi.getLegalMoves();

        if (legalMoves == 0L) {
            return -1;
        }

        if (Math.random() < 0.01) {
            int randomIndex = (int) (Math.random() * Long.bitCount(legalMoves));
            long moves = legalMoves;
            for (int i = 0; i < randomIndex; i++) {
                moves &= moves - 1;
            }
            return Long.numberOfTrailingZeros(moves);
        }

        int bestMove = -1;
        double bestVal = Double.NEGATIVE_INFINITY;

        long moves = legalMoves;
        while (moves != 0L) {
            int move = Long.numberOfTrailingZeros(moves);
            double value = logits[move];

            if (value > bestVal) {
                bestVal = value;
                bestMove = move;
            }

            moves &= moves - 1;
        }

        return bestMove;
    }

    @Override
    public long getMove(T game) {
        long[] input = game.getBoard();

        INDArray boardInput = Nd4j.create(new long[][] { input });
        INDArray prediction = model.output(boardInput);

        int move = pickLegalMove(prediction,(BitboardReversi) game);
        return move;
    }

    @Override
    public ReversiAIML<T> deepCopy() {
        return new ReversiAIML();
    }
}
