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

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * Interface to describe common behavior of all display components.
 *
 * @author Bernd Michaely
 */
public interface DisplayComponent
{
	Paint PAINT_GRADIENT = new LinearGradient(0, 0, 1, 0, true, NO_CYCLE,
		new Stop(0, Color.gray(0.85)), new Stop(1, Color.gray(0.92)));

	Background BACKGROUND_GRADIENT = new Background(
		new BackgroundFill(PAINT_GRADIENT, CornerRadii.EMPTY, Insets.EMPTY));

	/**
	 * Returns the containing node of the display.
	 *
	 * @return the display node
	 */
	Region getDisplay();
}
