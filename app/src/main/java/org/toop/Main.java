package org.toop;

import org.toop.app.App;
import org.toop.framework.machinelearning.NeuralNetwork;

public final class Main {
    static void main(String[] args) {
        App.run(args);
        NeuralNetwork nn = new NeuralNetwork();
        nn.init();
    }
}
