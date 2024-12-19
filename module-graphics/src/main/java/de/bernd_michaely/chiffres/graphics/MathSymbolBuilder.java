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
package de.bernd_michaely.chiffres.graphics;

import static java.util.Objects.requireNonNullElse;

/**
 * Abstract builder for math symbol graphics.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public abstract class MathSymbolBuilder
{
	private double fontSize;
	private Sign sign;

	/**
	 * Enumeration of the available symbols.
	 */
	public enum Sign
	{
		BLANK('\u0020'),
		PLUS_SIGN('\u002B'),
		MINUS_SIGN('\u2212'),
		MULTIPLICATION_SIGN('\u00D7'),
		DIVISION_SIGN('\u00F7'),
		EQUALS_SIGN('\u003D');

		private final char unicodeSymbol;

		private Sign(char unicodeSymbol)
		{
			this.unicodeSymbol = unicodeSymbol;
		}

		/**
		 * Returns a symbol for a unicode character.
		 *
		 * @return a unicode symbol character
		 */
		public char getUnicodeSymbol()
		{
			return unicodeSymbol;
		}

		public static Sign getDefaultSign()
		{
			return BLANK;
		}
	}

	/**
	 * Returns the font size.
	 *
	 * @return the font size
	 */
	public double getFontSize()
	{
		return fontSize;
	}

	/**
	 * Set the font size.
	 *
	 * @param fontSize the font size
	 */
	public void setFontSize(double fontSize)
	{
		this.fontSize = fontSize;
	}

	/**
	 * Returns the symbol to display.
	 *
	 * @return the symbol to display (BLANK by default)
	 */
	public Sign getSign()
	{
		return requireNonNullElse(sign, Sign.getDefaultSign());
	}

	/**
	 * Set the symbol to display.
	 *
	 * @param sign the symbol to display
	 */
	public void setSign(Sign sign)
	{
		this.sign = sign;
	}

	/**
	 * Creates a concrete line graphic.
	 *
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	protected abstract void buildLine(double startX, double startY, double endX, double endY);

	/**
	 * Creates a concrete circle graphic.
	 *
	 * @param centerX
	 * @param centerY
	 * @param radius
	 */
	protected abstract void buildCircle(double centerX, double centerY, double radius);
}
