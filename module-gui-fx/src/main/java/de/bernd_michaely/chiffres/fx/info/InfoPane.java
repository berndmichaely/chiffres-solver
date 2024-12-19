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
package de.bernd_michaely.chiffres.fx.info;

import de.bernd_michaely.chiffres.fx.util.MathSymbol;
import javafx.scene.layout.Region;

/**
 * Interface to describe a single info pane usable as a Tab or dialog window
 * content.
 *
 * @author Bernd Michaely
 */
public interface InfoPane
{
	double DEFAULT_FONT_SIZE = MathSymbol.DEFAULT_FONT_SIZE;
	double DEFAULT_INSET_SIZE = DEFAULT_FONT_SIZE * 15 / 13;

	/**
	 * Returns a title to display as a Tab or Window title.
	 *
	 * @return a title
	 */
	String getTitle();

	/**
	 * Returns the containing node of the display.
	 *
	 * @return the display node
	 */
	Region getDisplay();
}
