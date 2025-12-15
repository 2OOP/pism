package org.toop.game.machinelearning;

public class StateAction {
    long[] state;
    int action;
    public StateAction(long[] state, int action) {
        this.state = state;
        this.action = action;
    }
}
