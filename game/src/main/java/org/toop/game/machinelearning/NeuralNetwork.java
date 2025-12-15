package org.toop.game.machinelearning;

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
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.player.AbstractAI;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.games.reversi.BitboardReversi;
import org.toop.game.players.ArtificialPlayer;
import org.toop.game.players.ai.MiniMaxAI;
import org.toop.game.players.ai.RandomAI;
import org.toop.game.players.ai.ReversiAIML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class NeuralNetwork {

    private MultiLayerConfiguration conf;
    private MultiLayerNetwork model;
    private AbstractAI<BitboardReversi> opponentAI;
    private AbstractAI<BitboardReversi> opponentMM = new MiniMaxAI<>(6);
    private AbstractAI<BitboardReversi> opponentRand = new RandomAI<>();
    private AbstractAI<BitboardReversi> opponentAIML = new ReversiAIML<>();
    private Player[] playerSet = new Player[4];


    public NeuralNetwork() {}

    public void init(){
        initPlayers();

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

        model.setLearningRate(0.0003);
        trainingLoop();
        saveModel();
    }

    public void initPlayers(){
        playerSet[0] = new ArtificialPlayer<>(new MiniMaxAI<BitboardReversi>(6),"MiniMaxAI");
        playerSet[1] = new ArtificialPlayer<>(new RandomAI<BitboardReversi>(),"RandomAI");
        playerSet[2] = new ArtificialPlayer<>(new ReversiAIML<BitboardReversi>(),"MachineLearningAI");
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
        double epsilon = 0.05;



        long start =  System.nanoTime();

        for (int game = 0; game<totalGames; game++){
            char modelPlayer = random()<0.5?'B':'W';
            BitboardReversi reversi = new BitboardReversi(new Player[2]);
            opponentAI = getOpponentAI();
            List<StateAction> gameHistory = new ArrayList<>();
            PlayResult state = new PlayResult(GameState.NORMAL,reversi.getCurrentTurn());

            double reward = 0;

            while (state.state() != GameState.DRAW && state.state() != GameState.WIN){
                int curr = reversi.getCurrentTurn();
                long move;
                if (curr == modelPlayer) {
                    long[] input = reversi.getBoard();
                    if (Math.random() < epsilon) {
                        long moves = reversi.getLegalMoves();
                        move = (long) (Math.random() * Long.bitCount(moves) - .5f);
                    } else {
                        INDArray boardInput = Nd4j.create(new long[][]{input});
                        INDArray prediction = model.output(boardInput);

                        int location = pickLegalMove(prediction, reversi);
                        gameHistory.add(new StateAction(input, location));
                        move = location;
                    }
                }else{
                    move = opponentAI.getMove(reversi);
                }
                state = reversi.play(move);
            }

            //IO.println(model.params());
            BitboardReversi.Score score = reversi.getScore();
            int scoreDif = abs(score.black() - score.white());
            if (score.black() > score.white()){
                reward = 1 + ((scoreDif / 64.0) * 0.5);
            }else if (score.black() < score.white()){
                reward = -1 - ((scoreDif / 64.0) * 0.5);
            }else{
                reward = 0;
            }

            if (modelPlayer == 'W'){
                reward = -reward;
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

    private AbstractAI<BitboardReversi> getOpponentAI(){
        return switch ((int) (Math.random() * 4)) {
            case 0 -> opponentRand;
            case 1 -> opponentMM;
            case 2 -> opponentAIML;
            default -> opponentRand;
        };
    }

    private void trainFromHistory(StateAction step, double reward){
        double[] output = new double[64];
        output[step.action] = reward;

        DataSet ds = new DataSet(
                Nd4j.create(new long[][] { step.state }),
                Nd4j.create(new double[][] { output })
        );

        model.fit(ds);

    }
}
