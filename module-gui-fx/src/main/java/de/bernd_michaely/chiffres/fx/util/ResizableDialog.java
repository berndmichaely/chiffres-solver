/*
 * Copyright (C) 2024 Bernd Michaely (info@bernd-michaely.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.bernd_michaely.chiffres.fx.util;

import de.bernd_michaely.chiffres.fx.display.ScaleBox;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * Base class for resizable dialogs.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ResizableDialog
{
	private static final int GAP = (int) MathSymbol.DEFAULT_FONT_SIZE;
	private static final Insets insetsFirst = new Insets(GAP, 0, GAP, 0);
	private static final Insets insetsFollowing = new Insets(GAP, 0, GAP, GAP);
	private final Stage stage;
	private final Scene scene;
	private final BorderPane paneOuter;
	private final HBox paneButtons;
	private final ScaleBox scaleBox;
	private boolean committed;

	public ResizableDialog(Window owner, String title, Modality modality, Node content)
	{
		this.stage = new Stage(StageStyle.DECORATED);
		this.stage.initOwner(owner);
		this.stage.setTitle(title);
		this.stage.initModality(modality);
		this.paneOuter = new BorderPane(content);
		this.paneButtons = new HBox();
		this.paneButtons.setAlignment(Pos.CENTER);
		this.paneOuter.setBottom(this.paneButtons);
		this.scaleBox = new ScaleBox(paneOuter, false, true);
		this.stage.setOnShown(e -> postLayout());
		this.scene = new Scene(getRoot());
		this.stage.setScene(scene);
	}

	private Region getRoot()
	{
		return this.scaleBox.getDisplay();
	}

	public void close()
	{
		if (this.stage != null)
		{
			this.stage.fireEvent(new WindowEvent(
				this.stage, WindowEvent.WINDOW_CLOSE_REQUEST));
		}
	}

	public void setButtons(Button... buttons)
	{
		if (buttons != null)
		{
			final int n = buttons.length;
			this.paneButtons.getChildren().setAll(buttons);
			if (n > 0)
			{
				HBox.setMargin(buttons[0], insetsFirst);
				if (n > 1)
				{
					IntStream.range(1, n).forEach(i ->
						HBox.setMargin(buttons[i], insetsFollowing));
				}
			}
		}
	}

	public void setCloseButton()
	{
		final Button buttonClose = new Button("_Close");
		buttonClose.setDefaultButton(true);
		buttonClose.setCancelButton(true);
		buttonClose.setOnAction(e -> close());
		setButtons(buttonClose);
	}

	public void setOkCancelButtons(Runnable onOkAction)
	{
		final Button buttonOK = new Button("_OK");
		buttonOK.setOnAction(e ->
		{
			committed = true;
			if (onOkAction != null)
			{
				onOkAction.run();
			}
			close();
		});
		final Button buttonCancel = new Button("_Cancel");
		buttonOK.setDefaultButton(true);
		buttonCancel.setCancelButton(true);
		buttonCancel.setOnAction(e -> close());
		setButtons(buttonOK, buttonCancel);
	}

	/**
	 * Returns true, if the dialog was closed by pressing the OK button.
	 *
	 * @return true, if the dialog was closed by pressing the OK button
	 */
	public boolean isCommitted()
	{
		return committed;
	}

	private Stream<Button> streamButtons()
	{
		return paneButtons.getChildren().stream()
			.filter(node -> node instanceof Button)
			.map(node -> (Button) node);
	}

	private void equalizeButtonWidths()
	{
		final double withButtons = streamButtons()
			.map(Button::getWidth)
			.max(Double::compare).orElse(USE_COMPUTED_SIZE);
		streamButtons().forEach(button ->
		{
			button.setMinWidth(withButtons);
			button.setPrefWidth(withButtons);
			button.setMaxWidth(withButtons);
		});
	}

	private void postLayout()
	{
		getRoot().autosize();
		final double width = getRoot().getWidth();
		final double height = getRoot().getHeight();
		this.paneOuter.setMinSize(width, height);
		this.paneOuter.setPrefSize(width, height);
		this.scaleBox.initialize();
		equalizeButtonWidths();
		getDialog().sizeToScene();
	}

	public Stage getDialog()
	{
		return this.stage;
	}
}
