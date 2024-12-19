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

/**
 * Director for the MathSymbolBuilder.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class MathSymbolDirector
{
	private final MathSymbolBuilder builder;

	public MathSymbolDirector(MathSymbolBuilder builder)
	{
		this.builder = builder;
	}

	public void construct()
	{
		final MathSymbolBuilder.Sign sign = builder.getSign();
		if (sign == null || sign.equals(MathSymbolBuilder.Sign.BLANK))
		{
			return;
		}
		final double width = builder.getFontSize();
		final double height = builder.getFontSize();
		final double w_2 = width / 2;
		final double h_2 = height / 2;
		final double diff_a = builder.getFontSize() / 10;
		final double w_diff_a = width - diff_a;
		switch (sign)
		{
			case PLUS_SIGN:
				builder.buildLine(w_2, diff_a, w_2, height - diff_a);
				builder.buildLine(diff_a, h_2, w_diff_a, h_2);
				break;
			case MULTIPLICATION_SIGN:
				final double diff_m = builder.getFontSize() / 5;
				builder.buildLine(diff_m, diff_m, width - diff_m, height - diff_m);
				builder.buildLine(diff_m, height - diff_m, width - diff_m, diff_m);
				break;
			case MINUS_SIGN:
				builder.buildLine(diff_a, h_2, w_diff_a, h_2);
				break;
			case DIVISION_SIGN:
				final double radius = builder.getFontSize() / 10;
				final double dist = builder.getFontSize() / 5;
				builder.buildLine(diff_a, h_2, w_diff_a, h_2);
				builder.buildCircle(w_2, dist, radius);
				builder.buildCircle(w_2, builder.getFontSize() - dist, radius);
				break;
			case EQUALS_SIGN:
				final double dist1 = builder.getFontSize() / 3;
				final double dist2 = builder.getFontSize() - dist1;
				builder.buildLine(diff_a, dist1, w_diff_a, dist1);
				builder.buildLine(diff_a, dist2, w_diff_a, dist2);
				break;
			default:
				throw new AssertionError("Unknown Math Symbol");
		}
	}
}
