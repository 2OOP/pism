package org.toop.app.game.gameManagers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.GlobalEventBus;
import org.toop.framework.gameFramework.interfaces.UpdatesGameUI;
import org.toop.framework.gui.GUIEvents;
import org.toop.app.canvas.GameCanvas;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.GameThreadBehaviour.GameThreadStrategy;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.game.GameThreadBehaviour.OnlineThreadBehaviour;
import org.toop.framework.gameFramework.TurnBasedGameR;
import org.toop.framework.gameFramework.interfaces.SupportsOnlinePlay;
import org.toop.game.players.AbstractPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class GameManager<T extends TurnBasedGameR> implements UpdatesGameUI, GameThreadStrategy, SupportsOnlinePlay {
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
    protected GameManager(GameCanvas<T> canvas, AbstractPlayer[] players, T game, GameThreadStrategy gameThreadBehaviour, String gameType) {
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
        logger.debug("Starting GameManager");
        gameThreadBehaviour.start();;
    }

    protected void addLisener(Consumer<?> listener){
        listeners.add(listener);
    }

    public void stop(){
        logger.debug("Stopping GameManager");
        removeListeners();
        gameThreadBehaviour.stop();
    }

    public AbstractPlayer getCurrentPlayer(){
        return gameThreadBehaviour.getCurrentPlayer();
    };

    private void addListeners(){
        // Listen to requests to update game UI
        listeners.add(GlobalEventBus.subscribe(GUIEvents.UpdateGameCanvas.class, this::onUpdateGameUI));
        listeners.add(GlobalEventBus.subscribe(GUIEvents.GameFinished.class, this::onGameFinish));

        //eventFlow
        //        .listen(this::onUpdateGameUI)
        //        .listen(this::onGameFinish);
    }

    private void removeListeners(){
        for (Consumer<?> listener : listeners){
            GlobalEventBus.unsubscribe(listener);
        }
    }

    private void onUpdateGameUI(GUIEvents.UpdateGameCanvas event){
        logger.debug("Updating UI");
        this.updateUI();
    }

    private void onGameFinish(GUIEvents.GameFinished event){
        logger.debug("Game Finished");
        System.out.println("Game Finished");
        String name = event.winner() == -1 ? null : getPlayer(event.winner()).getName();
        primary.gameOver(event.winOrTie(), name);
        stop();
    }

    public AbstractPlayer getPlayer(int player){
        if (player < 0 || player >= players.length){
            throw new IllegalArgumentException("player out of range");
        }
        return players[player];
    }

    private boolean isOnline(){
        return this.gameThreadBehaviour instanceof OnlineThreadBehaviour;
    }

    public void yourTurn(NetworkEvents.YourTurnResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour) this.gameThreadBehaviour).yourTurn(event);
        }
    }

    public void moveReceived(NetworkEvents.GameMoveResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour) this.gameThreadBehaviour).moveReceived(event);
        }
    }

    public void gameFinished(NetworkEvents.GameResultResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour) this.gameThreadBehaviour).gameFinished(event);
        }
    }
}
