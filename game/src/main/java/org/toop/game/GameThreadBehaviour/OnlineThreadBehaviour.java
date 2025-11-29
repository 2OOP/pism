package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gui.GUIEvents;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.TurnBasedGameR;
import org.toop.game.players.Player;

public class OnlineThreadBehaviour extends ThreadBehaviourBase {
    private Player mainPlayer;

    public OnlineThreadBehaviour(TurnBasedGameR game, Player mainPlayer) {
        super(game);
        this.mainPlayer = mainPlayer;
    }

    @Override
    public void start() {
        new EventFlow().listen(NetworkEvents.YourTurnResponse.class, this::onYourTurn);
        new EventFlow().listen(NetworkEvents.GameMoveResponse.class, this::onMoveReceived);
    }

    @Override
    public void stop() {

    }

    @Override
    public Player getCurrentPlayer(){
        // TODO: Don't assume current player is main player, this can be solved by making sure player list is ordered according to game.
        return mainPlayer;
    }

    public void onYourTurn(NetworkEvents.YourTurnResponse response){
        int move = getValidMove(mainPlayer);

        new EventFlow().addPostEvent(NetworkEvents.SendMove.class, response.clientId(), (short) move).asyncPostEvent();
    }

    public void onMoveReceived(NetworkEvents.GameMoveResponse response){
        game.play(Integer.parseInt(response.move())); // Assumes first onMoveReceived is first player, should be right... right?
        new EventFlow().addPostEvent(GUIEvents.UpdateGameCanvas.class).postEvent();
    }
}
