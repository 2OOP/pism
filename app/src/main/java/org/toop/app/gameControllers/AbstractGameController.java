package org.toop.app.gameControllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.gameFramework.controller.UpdatesGameUI;
import org.toop.framework.gameFramework.view.GUIEvents;
import org.toop.app.canvas.GameCanvas;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.game.gameThreads.OnlineThreadBehaviour;
import org.toop.framework.gameFramework.model.game.AbstractGame;
import org.toop.framework.gameFramework.model.game.SupportsOnlinePlay;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractGameController<T extends AbstractGame<T>> implements UpdatesGameUI, ThreadBehaviour<T> {
    protected final EventFlow eventFlow = new EventFlow();

    protected final List<Consumer<?>> listeners = new ArrayList<>();

    // Logger for logging ofcourse
    protected final Logger logger = LogManager.getLogger(this.getClass());

    // Reference to primary view
    protected final GameView primary;

    // Reference to game canvas
    protected final GameCanvas<T> canvas;

    private final Player<T>[] players;         // List of players, can't be changed.
    protected final T game;       // Reference to game instance
    private final ThreadBehaviour<T> gameThreadBehaviour;

    // TODO: Change gameType to automatically happen with either dependency injection or something else.
    // TODO: Make visualisation of moves a behaviour.
    protected AbstractGameController(GameCanvas<T> canvas, Player<T>[] players, T game, ThreadBehaviour<T> gameThreadBehaviour, String gameType) {
        logger.info("Creating AbstractGameController");
        // Make sure player list matches expected size
        if (players.length != game.getPlayerCount()){
            logger.error("Player count mismatch");
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
        logger.info("Starting GameManager");
        gameThreadBehaviour.start();;
    }

    public void stop(){
        logger.info("Stopping GameManager");
        removeListeners();
        gameThreadBehaviour.stop();
    }

    public Player<T> getCurrentPlayer(){
        return game.getPlayer(getCurrentPlayerIndex());
    };

    public int getCurrentPlayerIndex(){
        return game.getCurrentTurn();
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

    public Player<T> getPlayer(int player){
        if (player < 0 || player >= players.length){
            logger.error("Invalid player index");
            throw new IllegalArgumentException("player out of range");
        }
        return players[player];
    }

    private boolean isOnline(){
        return this.gameThreadBehaviour instanceof SupportsOnlinePlay;
    }

    public void onYourTurn(NetworkEvents.YourTurnResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour<T>) this.gameThreadBehaviour).onYourTurn(event);
        }
    }

    public void onMoveReceived(NetworkEvents.GameMoveResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour<T>) this.gameThreadBehaviour).onMoveReceived(event);
        }
    }

    public void gameFinished(NetworkEvents.GameResultResponse event){
        if (isOnline()){
            ((OnlineThreadBehaviour<T>) this.gameThreadBehaviour).gameFinished(event);
        }
    }
}
