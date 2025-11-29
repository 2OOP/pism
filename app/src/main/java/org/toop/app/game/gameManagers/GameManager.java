package org.toop.app.game.gameManagers;

import org.toop.framework.gui.GUIEvents;
import org.toop.app.canvas.GameCanvas;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.GameThreadBehaviour.GameThreadStrategy;
import org.toop.app.game.UpdatesGameUI;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.game.TurnBasedGameR;
import org.toop.game.players.Player;

public abstract class GameManager implements UpdatesGameUI, GameThreadStrategy {
    // Reference to primary view
    protected final GameView primary;

    // Reference to game canvas
    protected final GameCanvas canvas;

    private final Player[] players;         // List of players, can't be changed.
    protected final TurnBasedGameR game;       // Reference to game instance
    private final GameThreadStrategy gameThreadBehaviour;

    // TODO: Change gameType to automatically happen with either dependency injection or something else.
    protected GameManager(GameCanvas canvas, Player[] players, TurnBasedGameR game, GameThreadStrategy gameThreadBehaviour, String gameType) {
        // Make sure player list matches expected size
        if (players.length != game.getPlayerCount()){
            throw new IllegalArgumentException("players and game's players must have same length");
        }

        this.canvas = canvas;
        this.players = players;
        this.game = game;
        this.gameThreadBehaviour = gameThreadBehaviour;

        primary = new GameView(null, null, null, gameType);

        addListeners();
    }

    public void start(){
        gameThreadBehaviour.start();
    }

    public void stop(){
        gameThreadBehaviour.stop();
    }

    public void onYourTurn(NetworkEvents.YourTurnResponse response){
        System.out.println("Your turn received");
        gameThreadBehaviour.onYourTurn(response);
    }

    private void addListeners(){
        // Listen to requests to update game UI
        new EventFlow().listen(this::onUpdateGameUI);
        // Listen to server your turn event
        new EventFlow().listen(this::onYourTurn);
    }

    private void onUpdateGameUI(GUIEvents.UpdateGameCanvas event){
        this.updateUI();
    }

}
