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
package de.bernd_michaely.chiffres.graphics.svg;

import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder;

/**
 * Concrete builder for SVG math symbol graphics.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class SvgMathSymbolBuilder extends MathSymbolBuilder
{
	private final SvgElementBuilder group;
	private final double offsetX, offsetY;

	SvgMathSymbolBuilder(float fontSize, Sign sign, double offsetX, double offsetY)
	{
		setFontSize(fontSize);
		setSign(sign);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.group = new SvgElementBuilder("g");
	}

	@Override
	protected void buildLine(double startX, double startY, double endX, double endY)
	{
		group.addSubElements(new SvgElementBuilder("line")
			.addAttribute("x1", (float) (offsetX + startX))
			.addAttribute("y1", (float) (offsetY + startY))
			.addAttribute("x2", (float) (offsetX + endX))
			.addAttribute("y2", (float) (offsetY + endY))
			.addAttribute("stroke", "black")
			.addAttribute("stroke-width", (float) (getFontSize() / 7)));
	}

	@Override
	protected void buildCircle(double centerX, double centerY, double radius)
	{
		group.addSubElements(new SvgElementBuilder("circle")
			.addAttribute("cx", (float) (offsetX + centerX))
			.addAttribute("cy", (float) (offsetY + centerY))
			.addAttribute("r", (float) radius)
			.addAttribute("fill", "black"));
	}

	SvgElementBuilder getResult()
	{
		return group;
	}
}
