package org.toop.app.view.views;

import org.toop.app.App;
import org.toop.app.view.View;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public final class QuitView extends View {
	public QuitView() {
		super(false, "bg-popup");
	}

	@Override
	public void setup() {
		final Text sureHeader = header();
		sureHeader.setText(AppContext.getString("are-you-sure"));

		final Button yesButton = button();
		yesButton.setText(AppContext.getString("yes"));
		yesButton.setOnAction(_ -> { App.quit(); });

		final Button noButton = button();
		noButton.setText(AppContext.getString("no"));
		noButton.setOnAction(_ -> { App.stopQuit(); });

		add(Pos.CENTER,
			fit(vbox(
				sureHeader,

				hbox(
					yesButton,
					noButton
				)
			))
		);
	}
}