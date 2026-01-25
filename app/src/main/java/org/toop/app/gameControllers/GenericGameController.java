package org.toop.app.gameControllers;

import javafx.application.Platform;
import javafx.geometry.Pos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.app.canvas.GameCanvas;
import org.toop.app.canvas.ReversiBitCanvas;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.eventbus.GlobalEventBus;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.gameFramework.controller.GameController;
import org.toop.framework.gameFramework.model.game.threadBehaviour.SupportsOnlinePlay;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.game.threadBehaviour.ThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.gameFramework.view.GUIEvents;
import org.toop.framework.networking.connection.events.NetworkEvents;
import org.toop.framework.game.players.LocalPlayer;

public class GenericGameController implements GameController {
    protected final EventFlow eventFlow = new EventFlow();

    // Logger for logging
    protected final Logger logger = LogManager.getLogger(this.getClass());

    // Reference to gameView view
    protected final GameView gameView;

    // Reference to game canvas
    protected final GameCanvas canvas;

    protected final TurnBasedGame game;       // Reference to game instance
    private final ThreadBehaviour gameThreadBehaviour;

    // TODO: Change gameType to automatically happen with either dependency injection or something else.
    public GenericGameController(GameCanvas canvas, TurnBasedGame game, ThreadBehaviour gameThreadBehaviour, String gameType) {
        logger.info("Creating: {}", this.getClass());

        this.canvas = canvas;
        this.game = game;
        this.gameThreadBehaviour = gameThreadBehaviour;

        // Tell thread how to send moves
        this.gameThreadBehaviour.setOnSendMove(
                (id, m) -> GlobalEventBus.get().post(new NetworkEvents.SendMove(id, (short)translateMove(m)))
        );

        // Tell thread how to update UI
        this.gameThreadBehaviour.setOnUpdateUI(() -> Platform.runLater(this::updateUI));

        // Change scene to game view
        gameView = new GameView(null, null, null, gameType);
        gameView.add(Pos.CENTER, canvas.getCanvas());
        WidgetContainer.getCurrentView().transitionNext(gameView, true);

        // Listen to updates
        logger.info("Game controller started listening");
        eventFlow
                .listen(GUIEvents.GameEnded.class, this::onGameFinish, false)
                .listen(GUIEvents.PlayerAttemptedMove.class, event -> {
                    logger.info("User attempting move {}", event.move());
                    logger.info("Current player's turn {}", getCurrentPlayer().getName());
                    logger.info("First player {}", game.getPlayer(0).getName());
                    logger.info("Username {}", getCurrentPlayer().getName());
                    logger.info("User is class {}, {}", getCurrentPlayer().getClass(), getCurrentPlayer() instanceof LocalPlayer);
                    if (getCurrentPlayer() instanceof LocalPlayer lp) {
                        try {
                            lp.setLastMove(event.move());
                        } catch (Exception e) {
                            IO.println(e);
                        }
                    }
                }, false);
    }

    public void start(){
        logger.info("Starting GameManager");
        updateUI();
        gameThreadBehaviour.start();
        logger.info("GameManager started");
    }

    public void stop(){
        logger.info("Stopping GameManager");
        removeListeners();
        gameThreadBehaviour.stop();
        logger.info("GameManager stopped");
    }

    public Player getCurrentPlayer(){
        return game.getPlayer(getCurrentPlayerIndex());
    }

    public int getCurrentPlayerIndex(){
        return game.getCurrentTurn();
    }

    protected long translateMove(int move){
        return 1L << move;
    }

    protected int translateMove(long move){
        return Long.numberOfTrailingZeros(move);
    }

    private void removeListeners(){
        eventFlow.unsubscribeAll();
    }

    private void onGameFinish(GUIEvents.GameEnded event){
        logger.info("OnlineTurnBasedGame Finished");
        String name = event.winner() == -1 ? null : getPlayer(event.winner()).getName();
        gameView.gameOver(event.winOrTie(), name);
        stop();
    }

    public Player getPlayer(int player){
        if (player < 0 || player > game.getPlayerCount()-1){ // TODO: Make game turn player count
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
            ((SupportsOnlinePlay) this.gameThreadBehaviour).onYourTurn(event.clientId());
        }
    }

    public void onMoveReceived(NetworkEvents.GameMoveResponse event){
        if (isOnline()){
            ((SupportsOnlinePlay) this.gameThreadBehaviour).onMoveReceived(
                    translateMove(Integer.parseInt(event.move())));
        }
    }

    public void gameFinished(NetworkEvents.GameResultResponse event){
        if (isOnline()){
            ((SupportsOnlinePlay) this.gameThreadBehaviour).gameFinished(event.condition());
        }
    }

    @Override
    public void sendMove(long clientId, long move) {
        new EventFlow().addPostEvent(NetworkEvents.SendMove.class, clientId, (short) Long.numberOfTrailingZeros(move)).asyncPostEvent();
    }

    @Override
    public void updateUI() {
        TurnBasedGame gameCopy = game.deepCopy();
        canvas.redraw(gameCopy);
        String gameType = game.getClass().getSimpleName().replace("Bitboard","");
        gameView.nextPlayer(true, getCurrentPlayer().getName(), game.getPlayer(1-getCurrentPlayerIndex()).getName(),gameType);
        if (gameType.equals("Reversi")) {
            BitboardReversi reversiGame = (BitboardReversi) game;
            BitboardReversi.Score reversiScore = reversiGame.getScore();
            gameView.setPlayer1Score(reversiScore.black());
            gameView.setPlayer2Score(reversiScore.white());
            if (getCurrentPlayer() instanceof LocalPlayer) {
                ((ReversiBitCanvas)canvas).drawLegalDots(gameCopy);
            }
        }
    }
}
