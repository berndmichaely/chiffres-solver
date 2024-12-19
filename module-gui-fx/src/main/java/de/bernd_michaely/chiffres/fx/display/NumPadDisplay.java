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
package de.bernd_michaely.chiffres.fx.display;

import de.bernd_michaely.chiffres.fx.display.ButtonCtrl.ButtonKey;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Class to construct a numerical input display.
 *
 * @author Bernd Michaely
 */
public class NumPadDisplay implements DisplayComponent
{
	private static final Logger logger = Logger.getLogger(
		NumPadDisplay.class.getName());
	private final ButtonCtrl buttonCtrl = new ButtonCtrl();
	private final Pane paneOuter;
	private final GridPane grid;

	public NumPadDisplay(EventHandler<ActionEvent> eventHandler)
	{
		this.grid = new GridPane();
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_START), 0, 0, 3, 1);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_100), 3, 0);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_007), 0, 1);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_008), 1, 1);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_009), 2, 1);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_075), 3, 1);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_004), 0, 2);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_005), 1, 2);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_006), 2, 2);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_050), 3, 2);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_001), 0, 3);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_002), 1, 3);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_003), 2, 3);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_025), 3, 3);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_000), 0, 4);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_CLEAR), 1, 4);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_CLEAR_ALL), 2, 4);
		this.grid.add(this.buttonCtrl.getButton(ButtonKey.BUTTON_010), 3, 4);
		this.grid.setPrefWidth(CalculationDisplay.MIN_WIDTH);
		this.grid.setPrefHeight(CalculationDisplay.MIN_HEIGHT);
		this.grid.setPadding(new Insets(35));
		this.grid.setHgap(15);
		this.grid.setVgap(15);
		this.buttonCtrl.forEach(button ->
		{
			GridPane.setHgrow(button, Priority.ALWAYS);
			GridPane.setVgrow(button, Priority.ALWAYS);
			button.setOnAction(eventHandler);
		});
		final Rectangle rectangle = new Rectangle();
		rectangle.setFill(PAINT_GRADIENT);
		rectangle.setArcWidth(15);
		rectangle.setArcHeight(15);
		final double prefWidth = this.grid.getPrefWidth();
		final double prefHeight = this.grid.getPrefHeight();
		rectangle.setWidth(prefWidth);
		rectangle.setHeight(prefHeight);
		final DropShadow dropShadowPanel = new DropShadow();
		dropShadowPanel.setOffsetX(5.0);
		dropShadowPanel.setOffsetY(5.0);
		final Reflection reflection = new Reflection();
		reflection.setInput(dropShadowPanel);
		this.paneOuter = new StackPane(rectangle, this.grid);
		this.paneOuter.setMinSize(prefWidth, prefHeight);
		this.paneOuter.setPrefSize(prefWidth, prefHeight);
		this.paneOuter.setEffect(reflection);
	}

	ButtonCtrl getButtonCtrl()
	{
		return this.buttonCtrl;
	}

	@Override
	public Region getDisplay()
	{
		return this.paneOuter;
	}
}
