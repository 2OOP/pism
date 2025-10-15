package org.toop.app.view.views;

import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public final class ErrorView extends View {
	private final String error;

	public ErrorView(String error) {
		super(false, "bg-popup");
		this.error = error;
	}

	@Override
	public void setup() {
		final Text errorHeader = header();
		errorHeader.setText(AppContext.getString("error"));

		final Text errorText = text();
		errorText.setText(error);

		final Button okButton = button();
		okButton.setText(AppContext.getString("ok"));
		okButton.setOnAction(_ -> { ViewStack.pop(); });

		add(Pos.CENTER,
			vboxFill(
				errorHeader,
				separator(),

				vspacer(),
				errorText,
				vspacer(),

				separator(),
				okButton
			)
		);
	}
}