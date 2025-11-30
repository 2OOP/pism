package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gui.GUIEvents;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.gameFramework.TurnBasedGameR;
import org.toop.framework.gameFramework.interfaces.SupportsOnlinePlay;
import org.toop.game.players.AbstractPlayer;
import org.toop.game.players.OnlinePlayer;

import java.util.Arrays;

public class OnlineThreadBehaviour extends ThreadBehaviourBase implements SupportsOnlinePlay {
    private AbstractPlayer mainPlayer;

    public OnlineThreadBehaviour(TurnBasedGameR game, AbstractPlayer[] players) {
        super(game);
        this.mainPlayer = getFirstNotOnlinePlayer(players);
    }

    private AbstractPlayer getFirstNotOnlinePlayer(AbstractPlayer[] players) {
        for (AbstractPlayer player : players){
            if (!(player instanceof OnlinePlayer)){
                return player;
            }
        }
        throw new RuntimeException("All players are online players");
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }

    @Override
    public AbstractPlayer getCurrentPlayer(){
        // TODO: Don't assume current player is main player, this can be solved by making sure player list is ordered according to game.
        return mainPlayer;
    }

    @Override
    public void yourTurn(NetworkEvents.YourTurnResponse event) {
        int move = mainPlayer.getMove(game.clone());
        // Got move
        new EventFlow().addPostEvent(NetworkEvents.SendMove.class, event.clientId(), (short) move).postEvent();
    }

    @Override
    public void moveReceived(NetworkEvents.GameMoveResponse event) {
        game.play(Integer.parseInt(event.move())); // Assumes first onMoveReceived is first player, should be right... right?
        new EventFlow().addPostEvent(GUIEvents.UpdateGameCanvas.class).postEvent();
    }

    @Override
    public void gameFinished(NetworkEvents.GameResultResponse event) {
        // TODO: Assumes last player won for easy. This information should really be gathered from the server message, or at least determined by game logic.
        if (!event.condition().equalsIgnoreCase("DRAW")) {
            new EventFlow().addPostEvent(GUIEvents.GameFinished.class, true, getCurrentPlayer().getPlayerIndex()).postEvent();
        }
        else{
            new EventFlow().addPostEvent(GUIEvents.GameFinished.class, false, -1).postEvent();
        }
    }
}
