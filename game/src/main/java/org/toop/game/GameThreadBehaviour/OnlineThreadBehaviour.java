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
    public void onYourTurn(NetworkEvents.YourTurnResponse response){
        System.out.println("Getting move from player");
        // Get a valid player move
        int move = getValidMove(mainPlayer);

        new EventFlow().addPostEvent(NetworkEvents.SendMove.class, response.clientId(), (short)move).asyncPostEvent();
        new EventFlow().addPostEvent(GUIEvents.UpdateGameCanvas.class).asyncPostEvent();
    }

}
