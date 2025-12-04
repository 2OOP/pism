package org.toop.app.gameControllers;

import javafx.geometry.Pos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.app.canvas.GameCanvas;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.controller.GameController;
import org.toop.framework.gameFramework.controller.UpdatesGameUI;
import org.toop.framework.gameFramework.model.game.SupportsOnlinePlay;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.gameFramework.view.GUIEvents;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.gameThreads.OnlineThreadBehaviour;
import org.toop.game.players.LocalPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GenericGameController<T extends TurnBasedGame<T>> implements GameController {
    protected final EventFlow eventFlow = new EventFlow();

    protected final List<Consumer<?>> listeners = new ArrayList<>();

    // Logger for logging ofcourse
    protected final Logger logger = LogManager.getLogger(this.getClass());

    // Reference to gameView view
    protected final GameView gameView;

    // Reference to game canvas
    protected final GameCanvas<T> canvas;

    protected final TurnBasedGame<T> game;       // Reference to game instance
    private final ThreadBehaviour gameThreadBehaviour;

    // TODO: Change gameType to automatically happen with either dependency injection or something else.
    public GenericGameController(GameCanvas<T> canvas, T game, ThreadBehaviour gameThreadBehaviour, String gameType) {
        logger.info("Creating: " + this.getClass());

        this.canvas = canvas;
        this.game = game;
        this.gameThreadBehaviour = gameThreadBehaviour;


        gameView = new GameView(null, null, null, gameType);
        gameView.add(Pos.CENTER, canvas.getCanvas());
        WidgetContainer.getCurrentView().transitionNext(gameView, true);
        addListeners();
    }

    public void start(){
        logger.info("Starting GameManager");
        updateUI();
        gameThreadBehaviour.start();
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
                .listen(GUIEvents.GameEnded.class, this::onGameFinish, false)
                .listen(GUIEvents.PlayerAttemptedMove.class, event -> {if (getCurrentPlayer() instanceof LocalPlayer lp){lp.setMove(event.move());}}, false);
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
        gameView.gameOver(event.winOrTie(), name);
        stop();
    }

    public Player<T> getPlayer(int player){
        if (player < 0 || player >= 2){ // TODO: Make game turn player count
            logger.error("Invalid player index");
            throw new IllegalArgumentException("player out of range");
        }
        return game.getPlayer(player);
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

    @Override
    public void updateUI() {
        canvas.redraw(game.deepCopy());
    }
}
