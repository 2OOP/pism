package org.toop.app.game.gameManagers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.gameFramework.interfaces.UpdatesGameUI;
import org.toop.framework.gameFramework.GUIEvents;
import org.toop.app.canvas.GameCanvas;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.GameThreadBehaviour.GameThreadStrategy;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.game.GameThreadBehaviour.OnlineThreadBehaviour;
import org.toop.framework.gameFramework.abstractClasses.TurnBasedGameR;
import org.toop.framework.gameFramework.interfaces.SupportsOnlinePlay;
import org.toop.game.players.AbstractPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class GameController<T extends TurnBasedGameR> implements UpdatesGameUI, GameThreadStrategy, SupportsOnlinePlay {
    protected final EventFlow eventFlow = new EventFlow();

    protected final List<Consumer<?>> listeners = new ArrayList<>();

    // Logger for logging ofcourse
    protected final Logger logger = LogManager.getLogger(this.getClass());

    // Reference to primary view
    protected final GameView primary;

    // Reference to game canvas
    protected final GameCanvas<T> canvas;

    private final AbstractPlayer[] players;         // List of players, can't be changed.
    protected final T game;       // Reference to game instance
    private final GameThreadStrategy gameThreadBehaviour;

    // TODO: Change gameType to automatically happen with either dependency injection or something else.
    // TODO: Make visualisation of moves a behaviour.
    protected GameController(GameCanvas<T> canvas, AbstractPlayer[] players, T game, GameThreadStrategy gameThreadBehaviour, String gameType) {
        logger.info("Creating GameController");
        // Make sure player list matches expected size
        if (players.length != game.getPlayerCount()){
            logger.error("Player count mismatch");
            throw new IllegalArgumentException("players and game's players must have same length");
        }

        this.canvas = canvas;
        this.players = players;
        this.game = game;
        this.gameThreadBehaviour = gameThreadBehaviour;

        // Let players know who they are
        for(int i = 0; i < players.length; i++){
            players[i].setPlayerIndex(i);
        }

        primary = new GameView(null, null, null, gameType);
        addListeners();
    }

    public void start(){
        logger.info("Starting GameManager");
        gameThreadBehaviour.start();;
    }

    public void stop(){
        logger.info("Stopping GameManager");
        removeListeners();
        gameThreadBehaviour.stop();
    }

    public AbstractPlayer getCurrentPlayer(){
        return gameThreadBehaviour.getCurrentPlayer();
    };

    public int getCurrentPlayerIndex(){
        return getCurrentPlayer().getPlayerIndex();
    }

    private void addListeners(){
        eventFlow
                .listen(GUIEvents.RefreshGameCanvas.class, this::onUpdateGameUI, false)
                .listen(GUIEvents.GameEnded.class, this::onGameFinish, false);
    }

    private void removeListeners(){
        eventFlow.unsubscribeAll();
    }

    private void onUpdateGameUI(GUIEvents.RefreshGameCanvas event){
        this.updateUI();
    }

    private void onGameFinish(GUIEvents.GameEnded event){
        logger.info("Game Finished");
        String name = event.winner() == -1 ? null : getPlayer(event.winner()).getName();
        primary.gameOver(event.winOrTie(), name);
        stop();
    }

    public AbstractPlayer getPlayer(int player){
        if (player < 0 || player >= players.length){
            logger.error("Invalid player index");
            throw new IllegalArgumentException("player out of range");
        }
        return players[player];
    }

    private boolean isOnline(){
        return this.gameThreadBehaviour instanceof OnlineThreadBehaviour;
    }

    @Override
    public void yourTurn(NetworkEvents.YourTurnResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour) this.gameThreadBehaviour).yourTurn(event);
        }
    }

    @Override
    public void moveReceived(NetworkEvents.GameMoveResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour) this.gameThreadBehaviour).moveReceived(event);
        }
    }

    @Override
    public void gameFinished(NetworkEvents.GameResultResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour) this.gameThreadBehaviour).gameFinished(event);
        }
    }
}
