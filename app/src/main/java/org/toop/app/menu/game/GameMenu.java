package org.toop.app.menu.game;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.toop.app.App;
import org.toop.app.menu.MainMenu;
import org.toop.app.menu.Menu;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;
import org.toop.local.LocalizationEvents;

import java.util.Locale;

public abstract class GameMenu extends Menu {
	protected final class Cell {
		public float x;
		public float y;

		public float width;
		public float height;

		public Cell(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;

			this.width = width;
			this.height = height;
		}

		public boolean check(float x, float y) {
			return x >= this.x && y >= this.y && x <= this.x + width && y <= this.y + height;
		}
	}

	protected final int size;

	protected final Canvas canvas;
	protected final GraphicsContext graphics;

	protected final int rows;
	protected final int columns;

	protected final int gapSize;

	protected final Cell[] cells;

    private Locale currentLocale = AppContext.getLocale();
    private final LocalizationAsset loc = ResourceManager.get("localization");
    private final Button hint,back;
	protected GameMenu(int rows, int columns, int gapSize) {

		final int size = Math.min(App.getWidth(), App.getHeight()) / 5 * 4;

		final Canvas canvas = new Canvas(size, size);

		final GraphicsContext graphics = canvas.getGraphicsContext2D();

		this.size = size;

		this.canvas = canvas;
		this.graphics = graphics;

		this.rows = rows;
		this.columns = columns;

		this.gapSize = gapSize;

		cells = new Cell[rows * columns];

		final float cellWidth = ((float)size - (rows - 1) * gapSize) / rows;
		final float cellHeight = ((float)size - (columns - 1) * gapSize) / rows;

		for (int y = 0; y < columns; y++) {
			final float startY = y * cellHeight + y * gapSize;

			for (int x = 0; x < rows; x++) {
				final float startX = x * cellWidth + x * gapSize;
				cells[y * rows + x] = new Cell(startX, startY, cellWidth, cellHeight);
			}
		}

		final Region background = createBackground();

		final Text player1 = createText("player_1", "Player 1");
		final Text player2 = createText("player_2", "Player 2");

		final HBox playersContainer = new HBox(100, player1, player2);
		playersContainer.setAlignment(Pos.TOP_CENTER);
		playersContainer.setPickOnBounds(false);
		playersContainer.setTranslateY(50);

		hint = createButton(loc.getString("gameMenuHint",currentLocale), () -> {});
		back = createButton(loc.getString("gameMenuBack",currentLocale), () -> { App.activate(new MainMenu()); });

		final VBox controlContainer = new VBox(hint, back);
		StackPane.setAlignment(controlContainer, Pos.BOTTOM_LEFT);
		controlContainer.setPickOnBounds(false);

		pane = new StackPane(background, canvas, playersContainer, controlContainer);
        try {
            new EventFlow()
                    .listen(this::handleChangeLanguage);

        }catch (Exception e){
            System.out.println("Something went wrong while trying to change the language.");
            throw e;
        }

    }
    private void handleChangeLanguage(LocalizationEvents.LanguageHasChanged event) {
        Platform.runLater(() -> {
            currentLocale = AppContext.getLocale();
            hint.setText(loc.getString("gameMenuHint",currentLocale));
            back.setText(loc.getString("gameMenuBack",currentLocale));
        });

    }
}