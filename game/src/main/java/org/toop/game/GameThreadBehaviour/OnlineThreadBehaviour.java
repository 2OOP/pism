package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gui.GUIEvents;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.TurnBasedGameR;
import org.toop.game.players.AbstractPlayer;

public class OnlineThreadBehaviour extends ThreadBehaviourBase {
    private AbstractPlayer mainPlayer;

    public OnlineThreadBehaviour(TurnBasedGameR game, AbstractPlayer mainPlayer) {
        super(game);
        this.mainPlayer = mainPlayer;
    }

    @Override
    public void start() {
        new EventFlow()
                .listen(this::onYourTurn)
                .listen(this::onMoveReceived)
                .listen(this::onGameWin);
    }

    @Override
    public void stop() {

    }

    @Override
    public AbstractPlayer getCurrentPlayer(){
        // TODO: Don't assume current player is main player, this can be solved by making sure player list is ordered according to game.
        return mainPlayer;
    }

    public void onYourTurn(NetworkEvents.YourTurnResponse response){
        int move = getValidMove(mainPlayer);

        new EventFlow().addPostEvent(NetworkEvents.SendMove.class, response.clientId(), (short) move).postEvent();
    }

    public void onMoveReceived(NetworkEvents.GameMoveResponse response){
        game.play(Integer.parseInt(response.move())); // Assumes first onMoveReceived is first player, should be right... right?
        new EventFlow().addPostEvent(GUIEvents.UpdateGameCanvas.class).postEvent();
    }

    public void onGameWin(NetworkEvents.GameResultResponse response){
        // TODO: Assumes last player won for easy. This information should really be gathered from the server message, or at least determined by game logic.
        if (response.condition().equalsIgnoreCase("WIN")) {
            new EventFlow().addPostEvent(GUIEvents.GameFinished.class, true, getCurrentPlayer()).postEvent();
        }
        else{
            new EventFlow().addPostEvent(GUIEvents.GameFinished.class, false, -1).postEvent();
        }
    }
}
