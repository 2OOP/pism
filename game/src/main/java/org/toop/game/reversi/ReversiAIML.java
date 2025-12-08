package org.toop.game.reversi;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.toop.game.AI;
import org.toop.game.records.Move;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.random;

public class ReversiAIML extends AI<Reversi>{

    MultiLayerNetwork model;

    public ReversiAIML() {
        InputStream is = getClass().getResourceAsStream("/reversi-model.zip");
        try {
            assert is != null;
            model = ModelSerializer.restoreMultiLayerNetwork(is);
        } catch (IOException e) {}
    }

    public Move findBestMove(Reversi reversi, int depth){
        int[] input = reversi.getBoardInt();

        INDArray boardInput = Nd4j.create(new int[][] { input });
        INDArray prediction = model.output(boardInput);

        int move = pickLegalMove(prediction,reversi);
        return new Move(move, reversi.getCurrentPlayer());
    }

    private int pickLegalMove(INDArray prediction, Reversi reversi) {
        double[] logits = prediction.toDoubleVector();
        Move[] legalMoves = reversi.getLegalMoves();

        if (legalMoves.length == 0) return -1;

        int bestMove = legalMoves[0].position();
        double bestVal = logits[bestMove];

        if (random() < 0.01){
            return legalMoves[(int)(random()*legalMoves.length-.5)].position();
        }
        for (Move move : legalMoves) {
            int pos = move.position();
            if (logits[pos] > bestVal) {
                bestMove = pos;
                bestVal = logits[pos];
            }
        }
        return bestMove;
    }
}
