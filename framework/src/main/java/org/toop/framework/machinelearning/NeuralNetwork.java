package org.toop.framework.machinelearning;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.toop.game.AI;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;
import org.toop.game.reversi.Reversi;
import org.toop.game.reversi.ReversiAI;
import org.toop.game.reversi.ReversiAISimple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class NeuralNetwork {

    private MultiLayerConfiguration conf;
    private MultiLayerNetwork model;
    private ReversiAI reversiAI =  new ReversiAI();
    private AI<Reversi> opponentRand = new ReversiAI();
    private AI<Reversi> opponentSimple = new ReversiAISimple();


    public NeuralNetwork() {}

    public void init(){
        conf = new NeuralNetConfiguration.Builder()
                .updater(new Adam(0.001))
                .weightInit(WeightInit.XAVIER) //todo understand
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(64)
                        .nOut(128)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .nIn(128)
                        .nOut(64)
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();
        model = new MultiLayerNetwork(conf);
        IO.println(model.params());
        loadModel();
        IO.println(model.params());
        model.init();
        IO.println(model.summary());

        model.setLearningRate(0.001);
        trainingLoop();
        saveModel();
    }

    public void saveModel(){
        File modelFile = new File("reversi-model.zip");
        try {
            ModelSerializer.writeModel(model, modelFile, true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadModel(){
        File modelFile = new File("reversi-model.zip");
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trainingLoop(){
        int totalGames = 5000;
        double epsilon = 0.1;

        long start =  System.nanoTime();

        for (int game = 0; game<totalGames; game++){
            Reversi reversi = new Reversi();
            List<StateAction> gameHistory = new ArrayList<>();
            GameState state = GameState.NORMAL;

            double reward = 0;

            while (state != GameState.DRAW && state != GameState.WIN){
                char curr = reversi.getCurrentPlayer();
                Move move;
                if (curr == 'B') {
                    int[] input = reversi.getBoardInt();
                    if (Math.random() < epsilon) {
                        Move[] moves = reversi.getLegalMoves();
                        move = moves[(int) (Math.random() * moves.length - .5f)];
                    } else {
                        INDArray boardInput = Nd4j.create(new int[][]{input});
                        INDArray prediction = model.output(boardInput);

                        int location = pickLegalMove(prediction, reversi);
                        gameHistory.add(new StateAction(input, location));
                        move = new Move(location, reversi.getCurrentPlayer());
                    }
                }else{
                    move = reversiAI.findBestMove(reversi,5);
                }
                state = reversi.play(move);
            }

            //IO.println(model.params());
            Reversi.Score score = reversi.getScore();
            int scoreDif = abs(score.player1Score() - score.player2Score());
            if (score.player1Score() > score.player2Score()){
                reward = 1 + ((scoreDif / 64.0) * 0.5);
            }else if (score.player1Score() < score.player2Score()){
                reward = -1 - ((scoreDif / 64.0) * 0.5);
            }else{
                reward = 0;
            }


            for (StateAction step : gameHistory){
                trainFromHistory(step, reward);
            }

            //IO.println("Wr: " + (double)p1wins/(game+1) + " draws: " + draws);
            if(game % 100 == 0){
                IO.println("Completed game " + game + " | Reward: " + reward);
                //IO.println(Arrays.toString(reversi.getBoardDouble()));
            }
        }
        long end  =  System.nanoTime();
        IO.println((end-start));
    }

    private boolean isInCorner(Move move){
        return move.position() == 0 || move.position() == 7 || move.position() == 56 || move.position() == 63;
    }

    private int pickLegalMove(INDArray prediction, Reversi reversi){
        double[] probs = prediction.toDoubleVector();
        Move[] legalMoves = reversi.getLegalMoves();

        if (legalMoves.length == 0) return -1;

        int bestMove = legalMoves[0].position();
        double bestVal = probs[bestMove];

        for (Move move : legalMoves){
            if (probs[move.position()] > bestVal){
                bestMove = move.position();
                bestVal = probs[bestMove];
            }
        }
        return bestMove;
    }

    private void trainFromHistory(StateAction step, double reward){
        double[] output = new double[64];
        output[step.action] = reward;

        DataSet ds = new DataSet(
                Nd4j.create(new int[][] { step.state }),
                Nd4j.create(new double[][] { output })
        );

        model.fit(ds);

    }
}
