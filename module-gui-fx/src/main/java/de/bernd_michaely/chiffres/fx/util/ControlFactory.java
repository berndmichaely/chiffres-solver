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

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;

/**
 * Utility class to create customized controls.
 *
 * @author Bernd Michaely
 */
public class ControlFactory
{
	/**
	 * Creates an input field to enter a natural number in the range of
	 * {@code [1..maxValue]}.
	 *
	 * @param maxValue the maximum value to enter in the input field
	 * @return an input control
	 */
	public static TextInputControl createNaturalNumberInput(int maxValue)
	{
		final TextField control = new TextField();
		control.setEditable(true);
		control.setAlignment(Pos.CENTER_RIGHT);
		control.setPrefColumnCount(1 + (int) Math.log10(maxValue));
		control.setTextFormatter(new TextFormatter<>(change ->
		{
			final String str = change.getControlNewText();
			if (str.isEmpty() /* || str.equals("+") */)
			{
				return change; // allow empty input
			}
			try
			{
				final int value = Integer.parseInt(str);
				final boolean isInputValid = (value >= 1) && (value <= maxValue);
				return isInputValid ? change : null;
			}
			catch (NumberFormatException ex)
			{
				return null; // reject change
			}
		}));
		return control;
	}
}
