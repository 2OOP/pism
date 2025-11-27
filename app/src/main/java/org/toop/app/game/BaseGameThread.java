package org.toop.app.game;

import org.toop.app.GameInformation;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.Game;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseGameThread<TGame extends Game, TAI, TCanvas> {
	protected final GameInformation information;
	protected final int myTurn;
	protected final Runnable onGameOver;
	protected final BlockingQueue<Game.Move> moveQueue;

	protected final TGame game;
	protected final TAI ai;

	protected final GameView primary;
	protected final TCanvas canvas;

	protected final AtomicBoolean isRunning = new AtomicBoolean(true);

	protected BaseGameThread(
		GameInformation information,
		int myTurn,
		Runnable onForfeit,
		Runnable onExit,
		Consumer<String> onMessage,
		Runnable onGameOver,
		Supplier<TGame> gameSupplier,
		Supplier<TAI> aiSupplier,
		Function<Consumer<Integer>, TCanvas> canvasFactory) {

		this.information = information;
		this.myTurn = myTurn;
		this.onGameOver = onGameOver;
		this.moveQueue = new LinkedBlockingQueue<>();

		this.game = gameSupplier.get();
		this.ai = aiSupplier.get();

        String type = information.type.getTypeToString();
        if (onForfeit == null || onExit == null) {
            primary = new GameView(null, () -> {
                isRunning.set(false);
                WidgetContainer.getCurrentView().transitionPrevious();
            }, null, type);
		} else {
			primary = new GameView(onForfeit, () -> {
				isRunning.set(false);
				onExit.run();
			}, onMessage, type);
		}

		this.canvas = canvasFactory.apply(this::onCellClicked);

		addCanvasToPrimary();

		WidgetContainer.getCurrentView().transitionNext(primary);

		if (onForfeit == null || onExit == null)
			new Thread(this::localGameThread).start();
		else
			new EventFlow()
				.listen(NetworkEvents.GameMoveResponse.class, this::onMoveResponse)
				.listen(NetworkEvents.YourTurnResponse.class, this::onYourTurnResponse);

		setGameLabels(myTurn == 0);
	}

	private void onCellClicked(int cell) {
		if (!isRunning.get()) return;

		final int currentTurn = getCurrentTurn();
		if (!information.players[currentTurn].isHuman) return;

		final char value = getSymbolForTurn(currentTurn);

		try {
			moveQueue.put(new Game.Move(cell, value));
		} catch (InterruptedException _) {}
	}

	protected void gameOver() {
		if (onGameOver != null) {
			isRunning.set(false);
			onGameOver.run();
		}
	}

	protected void setGameLabels(boolean isMe) {
		final int currentTurn = getCurrentTurn();
		final String turnName = getNameForTurn(currentTurn);

		primary.nextPlayer(
			isMe,
			information.players[isMe ? 0 : 1].name,
			turnName,
			information.players[isMe ? 1 : 0].name
		);
	}

	protected abstract void addCanvasToPrimary();

	protected abstract int getCurrentTurn();
	protected abstract char getSymbolForTurn(int turn);
	protected abstract String getNameForTurn(int turn);

	protected abstract void onMoveResponse(NetworkEvents.GameMoveResponse response);
	protected abstract void onYourTurnResponse(NetworkEvents.YourTurnResponse response);

	protected abstract void localGameThread();
}