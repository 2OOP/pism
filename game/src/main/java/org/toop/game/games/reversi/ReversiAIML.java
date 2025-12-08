package org.toop.game.games.reversi;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.random;

public class ReversiAIML extends AbstractAI<ReversiR> {

    MultiLayerNetwork model;

    public ReversiAIML() {
        InputStream is = getClass().getResourceAsStream("/reversi-model.zip");
        try {
            assert is != null;
            model = ModelSerializer.restoreMultiLayerNetwork(is);
        } catch (IOException e) {}
    }

    private int pickLegalMove(INDArray prediction, ReversiR reversi) {
        double[] logits = prediction.toDoubleVector();
        int[] legalMoves = reversi.getLegalMoves();

        if (legalMoves.length == 0) return -1;

        int bestMove = legalMoves[0];
        double bestVal = logits[bestMove];

        if (random() < 0.01){
            return legalMoves[(int)(random()*legalMoves.length-.5)];
        }
        for (int move : legalMoves) {
            if (logits[move] > bestVal) {
                bestMove = move;
                bestVal = logits[move];
            }
        }
        return bestMove;
    }

    @Override
    public int getMove(ReversiR game) {
        int[] input = game.getBoard();

        INDArray boardInput = Nd4j.create(new int[][] { input });
        INDArray prediction = model.output(boardInput);

        int move = pickLegalMove(prediction,game);
        return move;
    }
}
